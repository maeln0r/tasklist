package ru.maelnor.tasks.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.maelnor.tasks.entity.TaskEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Transactional
@TestPropertySource(properties = "repository.type=jdbc")
public class JdbcTaskEntityRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTaskRepository jdbcTaskRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tasks (id SERIAL PRIMARY KEY, name VARCHAR(255), completed BOOLEAN)");
        jdbcTemplate.execute("TRUNCATE TABLE tasks RESTART IDENTITY");
    }

    @Test
    void shouldSaveTask() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(false);

        jdbcTaskRepository.save(taskEntity);

        List<TaskEntity> taskEntities = jdbcTaskRepository.findAll();
        assertEquals(1, taskEntities.size());
        assertEquals("Test Task", taskEntities.getFirst().getName());
    }

    @Test
    void shouldFindTaskById() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(false);

        jdbcTaskRepository.save(taskEntity);

        Optional<TaskEntity> foundTask = jdbcTaskRepository.findById(1L);
        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getName());
    }

    @Test
    void shouldUpdateTask() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName("Initial Task");
        taskEntity.setCompleted(false);

        jdbcTaskRepository.save(taskEntity);

        taskEntity.setName("Updated Task");
        taskEntity.setCompleted(true);
        taskEntity.setId(1L);
        jdbcTaskRepository.update(taskEntity);

        Optional<TaskEntity> updatedTask = jdbcTaskRepository.findById(1L);
        assertTrue(updatedTask.isPresent());
        assertEquals("Updated Task", updatedTask.get().getName());
        assertTrue(updatedTask.get().isCompleted());
    }

    @Test
    void shouldDeleteTask() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName("Test Task");
        taskEntity.setCompleted(false);

        jdbcTaskRepository.save(taskEntity);

        jdbcTaskRepository.delete(1L);

        List<TaskEntity> taskEntities = jdbcTaskRepository.findAll();
        assertTrue(taskEntities.isEmpty());
    }

    @Test
    void shouldFindAllTasks() {
        TaskEntity taskEntity1 = new TaskEntity();
        taskEntity1.setName("Task 1");
        taskEntity1.setCompleted(false);

        TaskEntity taskEntity2 = new TaskEntity();
        taskEntity2.setName("Task 2");
        taskEntity2.setCompleted(true);

        jdbcTaskRepository.save(taskEntity1);
        jdbcTaskRepository.save(taskEntity2);

        List<TaskEntity> taskEntities = jdbcTaskRepository.findAll();
        assertEquals(2, taskEntities.size());
        assertEquals("Task 1", taskEntities.get(0).getName());
        assertEquals("Task 2", taskEntities.get(1).getName());
    }
}
