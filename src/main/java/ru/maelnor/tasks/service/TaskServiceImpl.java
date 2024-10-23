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
import java.util.UUID;

/**
 * Реализация интерфейса {@link TaskService}, использующая JDBC для работы с задачами.
 * Этот класс помечен как устаревший, так как использует JDBC, и рекомендуется переходить на JPA.
 */
@Service
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc", matchIfMissing = true)
@Deprecated
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param taskRepository репозиторий для работы с задачами
     * @param taskProducer   продюсер для отправки сообщений в Kafka
     */
    public TaskServiceImpl(TaskRepository taskRepository, TaskProducer taskProducer) {
        this.taskRepository = taskRepository;
        this.taskProducer = taskProducer;
    }

    /**
     * Возвращает список всех задач.
     *
     * @return список задач в виде моделей {@link TaskModel}
     */
    @Override
    public List<TaskModel> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toModel)
                .toList();
    }

    /**
     * Добавляет новую задачу и отправляет сообщение в Kafka о её создании.
     *
     * @param taskDto данные задачи для создания
     * @return созданная задача в виде модели {@link TaskModel}
     */
    @Override
    @Transactional
    public TaskModel addTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        taskRepository.save(taskEntity);
        taskProducer.sendMessage(taskDto, TaskStatus.NEW);
        return taskMapper.toModel(taskEntity);
    }

    /**
     * Обновляет существующую задачу и отправляет сообщение в Kafka о её обновлении.
     *
     * @param taskDto данные задачи для обновления
     */
    @Override
    @Transactional
    public void updateTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.toEntity(taskDto);
        taskProducer.sendMessage(taskDto, TaskStatus.UPDATED);
        taskRepository.update(taskEntity);
    }

    /**
     * Удаляет задачу по её идентификатору и отправляет сообщение в Kafka о её удалении.
     *
     * @param id идентификатор задачи
     */
    @Override
    @Transactional
    public void deleteTask(UUID id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        TaskDto taskDto = taskMapper.toDto(taskEntity);
        taskRepository.delete(id);
        taskProducer.sendMessage(taskDto, TaskStatus.DELETED);
    }

    /**
     * Возвращает задачу по её идентификатору, если она существует.
     *
     * @param id идентификатор задачи
     * @return объект {@link Optional}, содержащий задачу в виде модели {@link TaskModel}
     */
    @Override
    public Optional<TaskModel> getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(taskMapper::toModel);
    }

    /**
     * Фильтрация задач (заглушка).
     *
     * @param filter объект фильтрации {@link TaskFilter}
     * @return null (метод еще не реализован)
     */
    @Override
    public Page<TaskModel> filterBy(TaskFilter filter) {
        return null;
    }
}
