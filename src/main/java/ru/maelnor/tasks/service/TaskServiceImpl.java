package ru.maelnor.tasks.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.filter.TaskFilter;
import ru.maelnor.tasks.dto.kafka.TaskStatus;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.repository.TaskRepository;
import ru.maelnor.tasks.service.kafka.TaskProducer;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc", matchIfMissing = true)
@Deprecated
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;

    public TaskServiceImpl(TaskRepository taskRepository, TaskProducer taskProducer) {
        this.taskRepository = taskRepository;
        this.taskProducer = taskProducer;
    }

    @Override
    public List<TaskModel> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public TaskModel addTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        taskRepository.save(taskEntity);
        taskProducer.sendMessage(taskDto, TaskStatus.NEW);
        return taskMapper.toModel(taskEntity);
    }

    @Override
    @Transactional
    public void updateTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        taskProducer.sendMessage(taskDto, TaskStatus.UPDATED);
        taskRepository.update(taskEntity);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        TaskDto taskDto = taskMapper.toDto(taskEntity);
        taskRepository.delete(id);
        taskProducer.sendMessage(taskDto, TaskStatus.DELETED);
    }

    @Override
    public Optional<TaskModel> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toModel);
    }


    /**
     * Заглушка
     *
     * @param filter
     * @return
     */
    @Override
    public Page<TaskModel> filterBy(TaskFilter filter) {
        return null;
    }
}
