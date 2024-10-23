package ru.maelnor.tasks.repository;

import ru.maelnor.tasks.entity.TaskEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Общий интерфейс для управления сущностями {@link TaskEntity}.
 * Определяет методы для выполнения CRUD-операций над задачами.
 */
public interface TaskRepository {

    /**
     * Возвращает список всех задач.
     *
     * @return список сущностей {@link TaskEntity}
     */
    List<TaskEntity> findAll();

    /**
     * Сохраняет новую задачу.
     *
     * @param taskEntity сущность задачи для сохранения
     */
    void save(TaskEntity taskEntity);

    /**
     * Обновляет существующую задачу.
     *
     * @param taskEntity сущность задачи для обновления
     */
    void update(TaskEntity taskEntity);

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     */
    void delete(UUID id);

    /**
     * Удаляет все задачи.
     */
    void deleteAll();

    /**
     * Находит задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return объект {@link Optional}, содержащий найденную задачу, если она существует
     */
    Optional<TaskEntity> findById(UUID id);
}
