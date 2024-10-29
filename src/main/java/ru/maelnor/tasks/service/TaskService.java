package ru.maelnor.tasks.service;

import org.springframework.data.domain.Page;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;
import ru.maelnor.tasks.model.TaskFilterModel;
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
     * @param taskModel объект {@link TaskModel}, содержащий данные задачи
     * @return добавленная задача в виде модели {@link TaskModel}
     */
    TaskModel addTask(TaskModel taskModel);

    /**
     * Обновляет существующую задачу на основе переданных данных.
     *
     * @param taskModel объект {@link TaskModel}, содержащий обновленные данные задачи
     * @return обновленная задача в виде модели {@link TaskModel}
     */
    TaskModel updateTask(TaskModel taskModel);

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
     * @param filter объект {@link TaskFilterModel}, содержащий параметры фильтрации
     * @return страница задач, удовлетворяющих критериям фильтрации
     */
    Page<TaskModel> filterBy(TaskFilterModel filter);
}
