package ru.maelnor.tasks.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.maelnor.tasks.TaskAbstractTest;
import ru.maelnor.tasks.exception.TaskNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
@ActiveProfiles("test")
public class TaskWebControllerTestTask extends TaskAbstractTest {

    @Test
    void shouldDisplayListOfTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("pageTitle", "Список задач"))
                .andExpect(view().name("tasks/list"));
    }

    @Test
    void shouldShowAddTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Добавление"))
                .andExpect(view().name("tasks/add"));
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
                .andExpect(view().name("tasks/add"));
    }

    @Test
    void shouldShowEditTaskForm() throws Exception {
        UUID taskId = taskService.getAllTasks().getFirst().getId();
        mockMvc.perform(get("/tasks/edit/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Редактирование задачи с id: " + taskId))
                .andExpect(view().name("tasks/edit"));
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
                .andExpect(view().name("tasks/edit"));
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
