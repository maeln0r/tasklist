package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.filter.TaskFilter;
import ru.maelnor.tasks.dto.kafka.TaskStatus;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.repository.JpaTaskRepository;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.repository.specification.TaskSpecification;
import ru.maelnor.tasks.security.CustomUserDetails;
import ru.maelnor.tasks.service.kafka.TaskProducer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
@EnableCaching
@Slf4j
@RequiredArgsConstructor
public class DatabaseTaskService implements TaskService {

    private final JpaTaskRepository taskRepository;
    private final CacheManager cacheManager;
    private final TaskProducer taskProducer;
    private final CurrentUserService currentUserService;
    private final JpaUserRepository userRepository;

    @Override
    @Cacheable(value = "tasks")
    public List<TaskModel> getAllTasks() {
        List<TaskModel> tasks;
        CustomUserDetails user = currentUserService.getCurrentUser();
        if (user.isAdmin() || user.isManager()) {
            tasks = taskRepository.findAll().stream()
                    .map(taskMapper::toModel)
                    .toList();
        } else {
            tasks = taskRepository.findByOwnerId(user.getId()).stream()
                    .map(taskMapper::toModel)
                    .toList();
        }


        var cache = cacheManager.getCache("tasks");
        // Складываем задачи из разных запросов в одно место
        if (cache != null) {
            tasks.forEach(task -> cache.put(task.getId(), task));
        } else {
            log.warn("Кеширование недоступно");
        }

        return tasks;
    }

    @Override
    @Transactional
    @CachePut(value = "tasks", key = "#result.id")
    public TaskModel addTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        CustomUserDetails user = currentUserService.getCurrentUser();
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Недостаточно прав для удаления задачи"));
        taskEntity.setOwner(userEntity);
        TaskModel result = taskMapper.toModel(taskRepository.save(taskEntity));
        taskProducer.sendMessage(taskMapper.toDto(result), TaskStatus.NEW);
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#taskDto.id")
    public void updateTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        taskProducer.sendMessage(taskDto, TaskStatus.UPDATED);
        taskRepository.save(taskEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#id")
    public void deleteTask(UUID id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        CustomUserDetails user = currentUserService.getCurrentUser();
        TaskDto taskDto = taskMapper.toDto(taskEntity);
        if (user.isAdmin() ||
                taskEntity.getOwner().getId().equals(user.getId())) {
            taskRepository.deleteById(id);
            taskProducer.sendMessage(taskDto, TaskStatus.DELETED);
        } else {
            throw new AccessDeniedException("Недостаточно прав для удаления задачи");
        }
    }


    @Override
    @Cacheable(value = "tasks", key = "#id")
    public Optional<TaskModel> getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(taskMapper::toModel);
    }

    // todo: Разобраться с кешитрованием постранички...
    @Override
    @Cacheable(value = "tasks")
    public Page<TaskModel> filterBy(TaskFilter taskFilter) {
        return taskRepository.findAll(TaskSpecification.withFilter(taskFilter),
                PageRequest.of(
                        taskFilter.getPageNumber() != null ? taskFilter.getPageNumber() : 0,
                        taskFilter.getPageSize() != null ? taskFilter.getPageSize() : 10,
                        Sort.Direction.DESC,
                        "id"
                )
        ).map(taskMapper::toModel);
    }
}
