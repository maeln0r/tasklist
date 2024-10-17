package ru.maelnor.tasks.controller.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.repository.JpaTaskRepository;
import ru.maelnor.tasks.service.TaskService;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class TaskEntityRestControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private JpaTaskRepository taskRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        TaskDto dto = new TaskDto();
        dto.setId(null);
        dto.setName("Test Task");
        dto.setCompleted(false);
        taskService.addTask(dto);
    }

    @Test
    void shouldReturnAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Task"));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        Long taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    @Test
    void shouldCreateNewTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content("{\"name\":\"New Task\", \"completed\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Task"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldUpdateTask() throws Exception {
        Long taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Task\", \"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Task"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteTask() throws Exception {
        Long taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }


    @ParameterizedTest
    @MethodSource("provideTasksForTesting")
    void shouldValidateTaskNameWhenCreate(String name, boolean shouldPass) throws Exception {
        String taskJson = "{\"name\":\"" + name + "\",\"completed\":false}";

        if (shouldPass) {
            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskJson))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskJson))
                    .andExpect(status().isBadRequest());
        }
    }


    @ParameterizedTest
    @MethodSource("provideTasksForTesting")
    void shouldValidateTaskNameWhenUpdate(String name, boolean shouldPass) throws Exception {
        String taskJson = "{\"name\":\"" + name + "\",\"completed\":false}";

        if (!shouldPass) {
            mockMvc.perform(put("/api/tasks/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskJson))
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<Arguments> provideTasksForTesting() {
        return Stream.of(
                arguments("", false), // Пустое имя
                arguments(RandomStringUtils.randomAlphabetic(4), false), // Имя короче 5 символов
                arguments(RandomStringUtils.randomAlphabetic(256), false), // Имя длиной больше 255 символов
                arguments(RandomStringUtils.randomAlphabetic(10), true) // Корректное имя длиной 10 символов
        );
    }
}
