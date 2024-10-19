package ru.maelnor.tasks.repository;

import ru.maelnor.tasks.entity.TaskEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TaskRepository {
    List<TaskEntity> findAll();
    void save(TaskEntity taskEntity);
    void update(TaskEntity taskEntity);
    void delete(UUID id);
    void deleteAll();
    Optional<TaskEntity> findById(UUID id);
}
