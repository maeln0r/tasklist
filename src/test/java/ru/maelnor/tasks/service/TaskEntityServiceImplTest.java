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

    @BeforeEach
    void setUp() {
        taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(false);

        taskDto = new TaskDto();
        taskDto.setId(1L);
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
        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(1L);
    }

    @Test
    void shouldReturnTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));

        Optional<TaskModel> foundTask = taskService.getTaskById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getName());

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyOptionalWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TaskModel> foundTask = taskService.getTaskById(1L);

        assertFalse(foundTask.isPresent());

        verify(taskRepository, times(1)).findById(1L);
    }
}
