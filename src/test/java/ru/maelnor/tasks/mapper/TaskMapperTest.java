package ru.maelnor.tasks.mapper;

import org.junit.jupiter.api.Test;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.model.TaskModel;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {
    @Test
    void shouldMapTaskEntityToTaskDto() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID());
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(true);
        taskEntity.setDescription("Test Description");
        taskEntity.setCreatedAt(new Date());
        taskEntity.setUpdatedAt(new Date());
        taskEntity.setOwner(UserEntity.builder().id(UUID.randomUUID()).build());

        // Act
        TaskDto taskDto = TaskMapper.INSTANCE.toDto(taskEntity);

        // Assert
        assertNotNull(taskDto);
        assertEquals(taskEntity.getId(), taskDto.getId());
        assertEquals(taskEntity.getName(), taskDto.getName());
        assertEquals(taskEntity.isCompleted(), taskDto.isCompleted());
        assertEquals(taskEntity.getDescription(), taskDto.getDescription());
    }

    @Test
    void shouldMapTaskModelToTaskDto() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setName("Test Task");
        taskModel.setCompleted(true);
        taskModel.setDescription("Test Description");

        // Act
        TaskDto taskDto = TaskMapper.INSTANCE.toDto(taskModel);

        // Assert
        assertNotNull(taskDto);
        assertEquals(taskModel.getId(), taskDto.getId());
        assertEquals(taskModel.getName(), taskDto.getName());
        assertEquals(taskModel.isCompleted(), taskDto.isCompleted());
        assertEquals(taskModel.getDescription(), taskDto.getDescription());
    }

    @Test
    void shouldMapTaskDtoToTaskEntity() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setId(UUID.randomUUID());
        taskDto.setName("Test Task");
        taskDto.setCompleted(true);
        taskDto.setDescription("Test Description");

        // Act
        TaskEntity taskEntity = TaskMapper.INSTANCE.toEntity(taskDto);

        // Assert
        assertNotNull(taskEntity);
        assertEquals(taskDto.getId(), taskEntity.getId());
        assertEquals(taskDto.getName(), taskEntity.getName());
        assertEquals(taskDto.isCompleted(), taskEntity.isCompleted());
        assertEquals(taskDto.getDescription(), taskEntity.getDescription());
    }

    @Test
    void shouldMapTaskEntityToTaskModel() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID());
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(true);
        taskEntity.setDescription("Test Description");
        taskEntity.setCreatedAt(new Date());
        taskEntity.setUpdatedAt(new Date());
        taskEntity.setOwner(UserEntity.builder().id(UUID.randomUUID()).build());

        // Act
        TaskModel taskModel = TaskMapper.INSTANCE.toModel(taskEntity);

        // Assert
        assertNotNull(taskModel);
        assertEquals(taskEntity.getId(), taskModel.getId());
        assertEquals(taskEntity.getName(), taskModel.getName());
        assertEquals(taskEntity.isCompleted(), taskModel.isCompleted());
        assertEquals(taskEntity.getDescription(), taskModel.getDescription());
    }
}