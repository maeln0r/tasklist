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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

/**
 * Сервис для работы с задачами, использующий базу данных для хранения и управления задачами.
 * Поддерживает операции с кэшированием и отправкой сообщений в Kafka.
 */
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

    /**
     * Возвращает список всех задач в зависимости от ролей пользователя (администратор, менеджер или владелец).
     * Кэширует результаты для ускорения последующих запросов.
     *
     * @return список задач в виде моделей {@link TaskModel}
     */
    @Override
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
        if (cache != null) {
            tasks.forEach(task -> cache.put(task.getId(), task));
        } else {
            log.warn("Кеширование недоступно");
        }

        return tasks;
    }

    /**
     * Добавляет новую задачу и отправляет сообщение в Kafka.
     * Кэширует добавленную задачу.
     *
     * @param taskDto данные задачи для создания
     * @return созданная задача в виде модели {@link TaskModel}
     */
    @Override
    @Transactional
    @CachePut(value = "tasks", key = "#result.id")
    public TaskModel addTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        CustomUserDetails user = currentUserService.getCurrentUser();
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Недостаточно прав для добавления задачи"));
        taskEntity.setOwner(userEntity);
        TaskModel result = taskMapper.toModel(taskRepository.save(taskEntity));
        taskProducer.sendMessage(taskMapper.toDto(result), TaskStatus.NEW);
        return result;
    }

    /**
     * Обновляет существующую задачу и отправляет сообщение в Kafka.
     * Удаляет задачу из кэша.
     *
     * @param taskDto данные задачи для обновления
     */
    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#taskDto.id")
    public void updateTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskRepository.findById(taskDto.getId())
                .orElseThrow(() -> new TaskNotFoundException(taskDto.getId()));
        CustomUserDetails user = currentUserService.getCurrentUser();
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Недостаточно прав для обновления задачи"));

        if (user.isAdmin() || taskEntity.getOwner().getId().equals(user.getId())) {
            TaskEntity updatedTask = taskMapper.toEntity(taskDto);
            updatedTask.setOwner(userEntity);
            taskProducer.sendMessage(taskDto, TaskStatus.UPDATED);
            taskRepository.save(updatedTask);
        } else {
            throw new AccessDeniedException("Недостаточно прав для обновления задачи");
        }
    }

    /**
     * Удаляет задачу и отправляет сообщение в Kafka.
     * Удаляет задачу из кэша.
     *
     * @param id идентификатор задачи
     */
    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#id")
    public void deleteTask(UUID id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        CustomUserDetails user = currentUserService.getCurrentUser();
        TaskDto taskDto = taskMapper.toDto(taskEntity);
        if (user.isAdmin() || taskEntity.getOwner().getId().equals(user.getId())) {
            taskRepository.deleteById(id);
            taskProducer.sendMessage(taskDto, TaskStatus.DELETED);
        } else {
            throw new AccessDeniedException("Недостаточно прав для удаления задачи");
        }
    }

    /**
     * Возвращает задачу по идентификатору, если у пользователя есть доступ.
     * Кэширует результат запроса.
     *
     * @param id идентификатор задачи
     * @return объект {@link Optional}, содержащий задачу, если она найдена
     */
    @Override
    @Cacheable(value = "tasks", key = "#id")
    public Optional<TaskModel> getTaskById(UUID id) {
        Optional<TaskEntity> taskEntityOptional = taskRepository.findById(id);

        if (taskEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        TaskEntity taskEntity = taskEntityOptional.get();
        CustomUserDetails user = currentUserService.getCurrentUser();

        if (user.isAdmin() || user.isManager() || taskEntity.getOwner().getId().equals(user.getId())) {
            return Optional.of(taskMapper.toModel(taskEntity));
        } else {
            throw new AccessDeniedException("Недостаточно прав для просмотра задачи");
        }
    }

    /**
     * Фильтрует задачи на основе параметров фильтрации.
     * Поддерживает пагинацию и сортировку, кэширует результаты.
     *
     * @param taskFilter объект фильтрации {@link TaskFilter}
     * @return страница задач {@link Page}, удовлетворяющих критериям фильтрации
     */
    @Override
    public Page<TaskModel> filterBy(TaskFilter taskFilter) {
        Page<TaskModel> tasks;
        CustomUserDetails user = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(
                taskFilter.getPageNumber() != null ? taskFilter.getPageNumber() : 0,
                taskFilter.getPageSize() != null ? taskFilter.getPageSize() : 10,
                Sort.Direction.DESC,
                "id"
        );
        if (user.isAdmin() || user.isManager()) {
            tasks = taskRepository.findAll(TaskSpecification.withFilter(taskFilter), pageable)
                    .map(taskMapper::toModel);
        } else {
            tasks = taskRepository.findAll(
                            Specification.where(TaskSpecification.ownedBy(user.getId()))
                                    .and(TaskSpecification.withFilter(taskFilter)), pageable)
                    .map(taskMapper::toModel);
        }

        var cache = cacheManager.getCache("tasks");
        if (cache != null) {
            tasks.forEach(task -> cache.put(task.getId(), task));
        } else {
            log.warn("Кеширование недоступно");
        }

        return tasks;
    }
}
