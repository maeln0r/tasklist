package ru.maelnor.tasks.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тест JDBC через моки
 */
class JdbcTaskRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        TaskEntity task = createTaskEntity();
        List<TaskEntity> expectedTasks = List.of(task);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expectedTasks);

        List<TaskEntity> actualTasks = taskRepository.findAll();

        assertEquals(expectedTasks, actualTasks);
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    void testSave() {
        TaskEntity task = createTaskEntity();

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        taskRepository.save(task);

        verify(jdbcTemplate, times(1)).update(anyString(), any(Object[].class));
    }

    @Test
    void testUpdate() {
        TaskEntity task = createTaskEntity();

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        taskRepository.update(task);

        verify(jdbcTemplate, times(1)).update(anyString(), any(Object[].class));
    }

    @Test
    void testDelete() {
        UUID taskId = UUID.randomUUID();

        when(jdbcTemplate.update(anyString(), eq(taskId))).thenReturn(1);

        taskRepository.delete(taskId);

        verify(jdbcTemplate, times(1)).update(anyString(), eq(taskId));
    }

    @Test
    void testDeleteAll() {
        when(jdbcTemplate.update(anyString())).thenReturn(1);

        taskRepository.deleteAll();

        verify(jdbcTemplate, times(1)).update(anyString());
    }

    @Test
    void testFindById() {
        TaskEntity task = createTaskEntity();
        List<TaskEntity> tasks = List.of(task);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(task.getId()))).thenReturn(tasks);

        Optional<TaskEntity> actualTask = taskRepository.findById(task.getId());

        assertTrue(actualTask.isPresent());
        assertEquals(task, actualTask.get());
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(task.getId()));
    }

    private TaskEntity createTaskEntity() {
        TaskEntity task = new TaskEntity();
        task.setId(UUID.randomUUID());
        task.setName("Test Task");
        task.setCompleted(false);
        task.setDescription("Test Description");
        task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        UserEntity owner = new UserEntity();
        owner.setId(UUID.randomUUID());
        owner.setUsername("user");
        owner.setEmail("user@email.com");

        task.setOwner(owner);

        return task;
    }
}
