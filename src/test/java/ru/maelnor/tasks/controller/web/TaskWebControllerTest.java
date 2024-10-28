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

import java.text.MessageFormat;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
@ActiveProfiles("test")
public class TaskWebControllerTest extends TaskAbstractTest {

    @Test
    void shouldDisplayListOfTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("page", hasProperty("content", hasSize(greaterThan(0)))))
                .andExpect(model().attribute("pageTitle", "Список задач"))
                .andExpect(view().name("tasks/list"));
    }

    @Test
    void shouldDisplayFilteredListOfTasks() throws Exception {
        int pageNumber = 0;
        int pageSize = 5;
        String taskName = "Test Task";
        boolean completed = false;

        mockMvc.perform(get("/tasks")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("name", taskName)
                        .param("completed", String.valueOf(completed))
                        .param("ownerId", user.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageTitle", "Список задач"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("taskFilter"))
                .andExpect(view().name("tasks/list"))
                .andExpect(model().attribute("page", hasProperty("content", hasSize(greaterThan(0)))))
                .andExpect(model().attribute("taskFilter", hasProperty("pageNumber", is(pageNumber))))
                .andExpect(model().attribute("taskFilter", hasProperty("pageSize", is(pageSize))))
                .andExpect(model().attribute("taskFilter", hasProperty("name", is(taskName))))
                .andExpect(model().attribute("taskFilter", hasProperty("completed", is(completed))))
                .andExpect(model().attribute("taskFilter", hasProperty("ownerId", is(user.getId()))));

    }

    @Test
    void shouldThrowValidationExceptionIfTaskFilterPageDoesNotAllow() throws Exception {
        String taskName = "Test Task";
        boolean completed = false;

        mockMvc.perform(get("/tasks")
                        .param("name", taskName)
                        .param("completed", String.valueOf(completed))
                        .param("ownerId", user.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageTitle", "Список задач"))
                .andExpect(model().attributeExists("filterError"))
                .andExpect(model().attributeExists("taskFilter"))
                .andExpect(model().attributeDoesNotExist("page"))
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
    void shouldShowOwnTaskDetailPage() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(get("/tasks/view/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Просмотр задачи с id: " + taskId))
                .andExpect(view().name("tasks/view"));
    }

    @Test
    void shouldThrowTaskNotFoundExceptionOnViewFrom() throws Exception {
        UUID taskId = UUID.randomUUID();
        MvcResult result = mockMvc.perform(get("/tasks/view/{id}", taskId))
                .andExpect(status().isNotFound())
                .andReturn();
        assertInstanceOf(TaskNotFoundException.class, result.getResolvedException());
    }

    @Test
    void shouldThrowExceptionForAdminDetailPageForUser() throws Exception {
        UUID taskId = adminTask.getId();
        mockMvc.perform(get("/tasks/view/{id}", taskId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldShowUserTaskDetailPageForManager() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(get("/tasks/view/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Просмотр задачи с id: " + taskId))
                .andExpect(view().name("tasks/view"));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldShowUserTaskDetailPageForAdmin() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(get("/tasks/view/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Просмотр задачи с id: " + taskId))
                .andExpect(view().name("tasks/view"));
    }

    @Test
    void shouldShowEditTaskFormForOwnTask() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(get("/tasks/edit/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Редактирование задачи с id: " + taskId))
                .andExpect(view().name("tasks/edit"));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldShowUserEditTaskFormForAdmin() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(get("/tasks/edit/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("taskDto"))
                .andExpect(model().attribute("pageTitle", "Редактирование задачи с id: " + taskId))
                .andExpect(view().name("tasks/edit"));
    }

    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldThrowAccessDeniedFroUserEditTaskFormForManager() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(get("/tasks/edit/{id}", taskId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Access Denied"));
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
    void shouldUpdateOwnTask() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(put("/tasks/edit/{id}", taskId)
                        .param("name", "Updated Task")
                        .param("completed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldUpdateUserTaskByAdmin() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(put("/tasks/edit/{id}", taskId)
                        .param("name", "Updated Task")
                        .param("completed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    void shouldThrowExceptionWhenUpdateAdminTaskByUser() throws Exception {
        UUID taskId = adminTask.getId();
        mockMvc.perform(put("/tasks/edit/{id}", taskId)
                        .param("name", "Updated Task")
                        .param("completed", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Недостаточно прав для просмотра задачи"));
    }


    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldThrowExceptionWhenUpdateUserTaskByManager() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(put("/tasks/edit/{id}", taskId)
                        .param("name", "Updated Task")
                        .param("completed", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Недостаточно прав для обновления задачи"));
    }

    @Test
    void shouldReturnValidationErrorsOnUpdateTask() throws Exception {
        UUID taskId = task.getId();
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
    void shouldDeleteOwnTask() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldDeleteUserTaskByAdmin() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    void shouldThrowExceptionWhenDeleteAdminTaskByUser() throws Exception {
        UUID taskId = adminTask.getId();
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Недостаточно прав для просмотра задачи"));
    }

    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldThrowExceptionWhenDeleteUserTaskByManager() throws Exception {
        UUID taskId = task.getId();
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Недостаточно прав для удаления задачи"));
    }


}
