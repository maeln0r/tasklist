package ru.maelnor.tasks.mapper;

import org.junit.jupiter.api.Test;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.TaskEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskEntityMapperTest {

    @Test
    void shouldMapTaskToTaskDto() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(true);

        // Act
        TaskDto taskDto = TaskMapper.INSTANCE.toDto(taskEntity);

        // Assert
        assertNotNull(taskDto);
        assertEquals(taskEntity.getId(), taskDto.getId());
        assertEquals(taskEntity.getName(), taskDto.getName());
        assertEquals(taskEntity.isCompleted(), taskDto.isCompleted());
    }

    @Test
    void shouldMapTaskDtoToTask() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task");
        taskDto.setCompleted(true);

        // Act
        TaskEntity taskEntity = TaskMapper.INSTANCE.toEntity(taskDto);

        // Assert
        assertNotNull(taskEntity);
        assertEquals(taskDto.getId(), taskEntity.getId());
        assertEquals(taskDto.getName(), taskEntity.getName());
        assertEquals(taskDto.isCompleted(), taskEntity.isCompleted());
    }
}
