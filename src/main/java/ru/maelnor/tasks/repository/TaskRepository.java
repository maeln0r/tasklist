package ru.maelnor.tasks.repository;

import ru.maelnor.tasks.entity.TaskEntity;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<TaskEntity> findAll();
    void save(TaskEntity taskEntity);
    void update(TaskEntity taskEntity);
    void delete(Long id);
    Optional<TaskEntity> findById(Long id);
}
