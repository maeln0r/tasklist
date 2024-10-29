package ru.maelnor.tasks.mapper;

import org.junit.jupiter.api.Test;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.projection.TaskSummary;

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
    void shouldMapTaskModelToTaskEntity() {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setName("Test Task");
        taskModel.setCompleted(true);
        taskModel.setDescription("Test Description");

        // Act
        TaskEntity taskEntity = TaskMapper.INSTANCE.toEntity(taskModel);

        // Assert
        assertNotNull(taskEntity);
        assertEquals(taskModel.getId(), taskEntity.getId());
        assertEquals(taskModel.getName(), taskEntity.getName());
        assertEquals(taskModel.isCompleted(), taskEntity.isCompleted());
        assertEquals(taskModel.getDescription(), taskEntity.getDescription());
    }

    @Test
    void shouldMapTaskSummaryToTaskEntity() {
        UUID taskId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(UUID.randomUUID()).build();
        TaskSummary taskSummary = new TaskSummary() {
            @Override
            public UUID getId() {
                return taskId;
            }

            @Override
            public String getName() {
                return "Test Task";
            }

            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public UserEntity getOwner() {
                return userEntity;
            }
        };

        // Act
        TaskEntity taskEntity = TaskMapper.INSTANCE.toEntity(taskSummary);

        // Assert
        assertNotNull(taskEntity);
        assertEquals(taskSummary.getId(), taskEntity.getId());
        assertEquals(taskSummary.getName(), taskEntity.getName());
        assertEquals(taskSummary.isCompleted(), taskEntity.isCompleted());
        assertEquals(taskSummary.getOwner(), taskEntity.getOwner());
    }

    @Test
    void shouldMapTaskDtoToTaskModel() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setId(UUID.randomUUID());
        taskDto.setName("Test Task");
        taskDto.setCompleted(true);
        taskDto.setDescription("Test Description");

        // Act
        TaskModel taskModel = TaskMapper.INSTANCE.toModel(taskDto);

        // Assert
        assertNotNull(taskModel);
        assertEquals(taskDto.getId(), taskModel.getId());
        assertEquals(taskDto.getName(), taskModel.getName());
        assertEquals(taskDto.isCompleted(), taskModel.isCompleted());
        assertEquals(taskDto.getDescription(), taskModel.getDescription());
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

    @Test
    void shouldMapTaskSummaryToTaskModel() {
        UUID taskId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(UUID.randomUUID()).build();
        TaskSummary taskSummary = new TaskSummary() {
            @Override
            public UUID getId() {
                return taskId;
            }

            @Override
            public String getName() {
                return "Test Task";
            }

            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public UserEntity getOwner() {
                return userEntity;
            }
        };

        // Act
        TaskModel taskModel = TaskMapper.INSTANCE.toModel(taskSummary);

        // Assert
        assertNotNull(taskModel);
        assertEquals(taskSummary.getId(), taskModel.getId());
        assertEquals(taskSummary.getName(), taskModel.getName());
        assertEquals(taskSummary.isCompleted(), taskModel.isCompleted());
    }
}