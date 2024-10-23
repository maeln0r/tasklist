package ru.maelnor.tasks.service;

import org.springframework.data.domain.Page;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.dto.filter.TaskFilter;
import ru.maelnor.tasks.model.TaskModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс для управления задачами. Определяет основные операции, такие как добавление, обновление, удаление и получение задач.
 */
public interface TaskService {

    TaskMapper taskMapper = TaskMapper.INSTANCE;

    /**
     * Возвращает список всех задач, доступных пользователю.
     *
     * @return список задач в виде моделей {@link TaskModel}
     */
    List<TaskModel> getAllTasks();

    /**
     * Добавляет новую задачу на основе переданных данных.
     *
     * @param taskDto объект {@link TaskDto}, содержащий данные задачи
     * @return добавленная задача в виде модели {@link TaskModel}
     */
    TaskModel addTask(TaskDto taskDto);

    /**
     * Обновляет существующую задачу на основе переданных данных.
     *
     * @param taskDto объект {@link TaskDto}, содержащий обновленные данные задачи
     */
    void updateTask(TaskDto taskDto);

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     */
    void deleteTask(UUID id);

    /**
     * Возвращает задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return объект {@link Optional}, содержащий задачу, если она найдена
     */
    Optional<TaskModel> getTaskById(UUID id);

    /**
     * Фильтрует задачи на основе переданных параметров фильтрации.
     *
     * @param filter объект {@link TaskFilter}, содержащий параметры фильтрации
     * @return страница задач, удовлетворяющих критериям фильтрации
     */
    Page<TaskModel> filterBy(TaskFilter filter);
}
