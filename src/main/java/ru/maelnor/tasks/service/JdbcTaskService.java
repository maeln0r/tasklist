package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;
import ru.maelnor.tasks.dto.kafka.TaskStatus;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.model.TaskFilterModel;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.repository.TaskRepository;
import ru.maelnor.tasks.security.CustomUserDetails;
import ru.maelnor.tasks.service.kafka.TaskProducer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация интерфейса {@link TaskService}, использующая JDBC для работы с задачами.
 * Этот класс помечен как устаревший, так как использует JDBC, и рекомендуется переходить на JPA.
 * @todo: добавить реализацию
 */
@Service
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
@Deprecated
@RequiredArgsConstructor
public class JdbcTaskService implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;
    private final CurrentUserService currentUserService;
    private final JpaUserRepository userRepository;

    /**
     * Возвращает список всех задач.
     *
     * @return список задач в виде моделей {@link TaskModel}
     */
    @Override
    public List<TaskModel> getAllTasks() {
        return null;
    }

    /**
     * Добавляет новую задачу и отправляет сообщение в Kafka о её создании.
     *
     * @param taskModel данные задачи для создания
     * @return созданная задача в виде модели {@link TaskModel}
     */
    @Override
    @Transactional
    public TaskModel addTask(TaskModel taskModel) {
        return null;
    }

    /**
     * Обновляет существующую задачу и отправляет сообщение в Kafka о её обновлении.
     *
     * @param taskModel данные задачи для обновления
     */
    @Override
    @Transactional
    public TaskModel updateTask(TaskModel taskModel) {
        return null;
    }

    /**
     * Удаляет задачу по её идентификатору и отправляет сообщение в Kafka о её удалении.
     *
     * @param id идентификатор задачи
     */
    @Override
    @Transactional
    public void deleteTask(UUID id) {
    }

    /**
     * Возвращает задачу по её идентификатору, если она существует.
     *
     * @param id идентификатор задачи
     * @return объект {@link Optional}, содержащий задачу в виде модели {@link TaskModel}
     */
    @Override
    public Optional<TaskModel> getTaskById(UUID id) {
        return Optional.empty();
    }

    /**
     * Фильтрация задач (заглушка).
     *
     * @param filter объект фильтрации {@link TaskFilterModel}
     * @return null (метод еще не реализован)
     */
    @Override
    public Page<TaskModel> filterBy(TaskFilterModel filter) {
        return null;
    }
}
