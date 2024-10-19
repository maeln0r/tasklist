package ru.maelnor.tasks.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.repository.TaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskEntityServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskEntity taskEntity;
    private TaskDto taskDto;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(false);

        taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setName("Test Task");
        taskDto.setCompleted(false);
    }

    @Test
    void shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(taskEntity));

        List<TaskModel> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getName());

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void shouldAddTask() {
        doNothing().when(taskRepository).save(any(TaskEntity.class));

        TaskModel createdTask = taskService.addTask(taskDto);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getName());

        verify(taskRepository, times(1)).save(taskEntity);
    }

    @Test
    void shouldUpdateTask() {

        taskService.updateTask(taskDto);

        verify(taskRepository, times(1)).update(taskEntity);
    }

    @Test
    void shouldDeleteTask() {
        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).delete(taskId);
    }

    @Test
    void shouldReturnTaskById() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        Optional<TaskModel> foundTask = taskService.getTaskById(taskId);

        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getName());

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void shouldReturnEmptyOptionalWhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<TaskModel> foundTask = taskService.getTaskById(taskId);

        assertFalse(foundTask.isPresent());

        verify(taskRepository, times(1)).findById(taskId);
    }
}
