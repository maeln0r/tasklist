package ru.maelnor.tasks.controller.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.service.TaskService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class TaskEntityWebControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        TaskDto dto = new TaskDto();
        dto.setId(null);
        dto.setName("Test Task");
        dto.setCompleted(false);
        taskService.addTask(dto);
    }

    @Test
    void shouldDisplayListOfTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("pageTitle", "Список дел"))
                .andExpect(view().name("layout :: mainPage(page='tasks/list', fragment='content')"));
    }

    @Test
    void shouldShowAddTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Добавление"))
                .andExpect(view().name("layout :: mainPage(page='tasks/add', fragment='content')"));
    }

    @Test
    void shouldAddNewTask() throws Exception {
        mockMvc.perform(post("/tasks/add")
                        .param("name", "New Task")
                        .param("completed", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    void shouldReturnValidationErrorsOnAddTask() throws Exception {
        mockMvc.perform(post("/tasks/add")
                        .param("name", "")) // Пустое имя вызывает ошибку валидации
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("taskDto", "name"))
                .andExpect(view().name("layout :: mainPage(page='tasks/add', fragment='content')"));
    }

    @Test
    void shouldShowEditTaskForm() throws Exception {
        UUID taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(get("/tasks/edit/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Редактирование задачи с id: " + taskId))
                .andExpect(view().name("layout :: mainPage(page='tasks/edit', fragment='content')"));
    }

    @Test
    void shouldThrowTaskNotFoundExceptionOnEditFrom() throws Exception {
        UUID taskId = UUID.randomUUID();
        MvcResult result = mockMvc.perform(get("/tasks/edit/{id}", taskId))
                .andExpect(status().isNotFound())
                .andReturn();
        assertInstanceOf(TaskNotFoundException.class, result.getResolvedException());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        UUID taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(put("/tasks/edit/{id}", taskId)
                        .param("name", "Updated Task")
                        .param("completed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    void shouldReturnValidationErrorsOnUpdateTask() throws Exception {
        UUID taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(put("/tasks/edit/{id}", taskId)
                        .param("name", "")) // Пустое имя вызывает ошибку валидации
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("taskDto", "name"))
                .andExpect(view().name("layout :: mainPage(page='tasks/edit', fragment='content')"));
    }

    @Test
    void shouldThrowTaskNotFoundExceptionOnUpdateTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        MvcResult result = mockMvc.perform(put("/tasks/edit/{id}", taskId))
                .andExpect(status().isNotFound())
                .andReturn();
        assertInstanceOf(TaskNotFoundException.class, result.getResolvedException());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        UUID taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }
}
