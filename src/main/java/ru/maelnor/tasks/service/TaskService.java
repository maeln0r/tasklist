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

public interface TaskService {
    TaskMapper taskMapper = TaskMapper.INSTANCE;
    List<TaskModel> getAllTasks();
    TaskModel addTask(TaskDto taskDto);
    void updateTask(TaskDto taskDto);
    void deleteTask(UUID id);
    Optional<TaskModel> getTaskById(UUID id);
    Page<TaskModel> filterBy(TaskFilter filter);
}
