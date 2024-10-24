package ru.maelnor.tasks.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.kafka.TaskStatus;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.repository.TaskRepository;
import ru.maelnor.tasks.service.kafka.TaskProducer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {
    UUID taskId;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskProducer taskProducer;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTasks() {
        TaskEntity taskEntity = createTaskEntity();
        TaskModel taskModel = createTaskModel();

        when(taskRepository.findAll()).thenReturn(List.of(taskEntity));
        when(taskMapper.toModel(taskEntity)).thenReturn(taskModel);

        List<TaskModel> actualTasks = taskService.getAllTasks();

        assertEquals(1, actualTasks.size());
        assertEquals(taskModel, actualTasks.get(0));

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testAddTask() {
        TaskDto taskDto = createTaskDto();
        TaskEntity taskEntity = createTaskEntity();
        TaskModel taskModel = createTaskModel();

        when(taskMapper.toEntity(taskDto)).thenReturn(taskEntity);
        when(taskMapper.toModel(taskEntity)).thenReturn(taskModel);
        doNothing().when(taskProducer).sendMessage(taskDto, TaskStatus.NEW);

        TaskModel createdTask = taskService.addTask(taskDto);

        assertEquals(taskModel, createdTask);

        verify(taskRepository, times(1)).save(taskEntity);
        verify(taskProducer, times(1)).sendMessage(taskDto, TaskStatus.NEW);
    }

    @Test
    void testUpdateTask() {
        TaskDto taskDto = createTaskDto();
        TaskEntity taskEntity = createTaskEntity();

        when(taskMapper.toEntity(taskDto)).thenReturn(taskEntity);
        doNothing().when(taskProducer).sendMessage(taskDto, TaskStatus.UPDATED);

        taskService.updateTask(taskDto);

        verify(taskProducer, times(1)).sendMessage(taskDto, TaskStatus.UPDATED);
        verify(taskRepository, times(1)).update(taskEntity);
    }

    @Test
    void testDeleteTask() {
        UUID taskId = UUID.randomUUID();
        TaskEntity taskEntity = createTaskEntity();
        TaskDto taskDto = createTaskDto();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskMapper.toDto(taskEntity)).thenReturn(taskDto);
        doNothing().when(taskProducer).sendMessage(taskDto, TaskStatus.DELETED);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(taskId);
        verify(taskProducer, times(1)).sendMessage(taskDto, TaskStatus.DELETED);
    }

    @Test
    void testDeleteTaskThrowsExceptionWhenNotFound() {
        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(any(UUID.class));
        verify(taskProducer, never()).sendMessage(any(TaskDto.class), any(TaskStatus.class));
    }

    @Test
    void testGetTaskById() {
        UUID taskId = UUID.randomUUID();
        TaskEntity taskEntity = createTaskEntity();
        TaskModel taskModel = createTaskModel();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskMapper.toModel(taskEntity)).thenReturn(taskModel);

        Optional<TaskModel> foundTask = taskService.getTaskById(taskId);

        assertTrue(foundTask.isPresent());
        assertEquals(taskModel, foundTask.get());

        verify(taskRepository, times(1)).findById(taskId);
    }

    private TaskEntity createTaskEntity() {
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setName("Test Task");
        task.setCompleted(false);
        task.setDescription("Test Description");
        return task;
    }

    private TaskModel createTaskModel() {
        return new TaskModel(taskId, "Test Task", "Test Description", false);
    }

    private TaskDto createTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setName("Test Task");
        taskDto.setDescription("Test Description");
        return taskDto;
    }
}
