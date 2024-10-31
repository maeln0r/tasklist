package ru.maelnor.tasks.controller.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.maelnor.tasks.TaskAbstractTest;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
public class TaskRestControllerTest extends TaskAbstractTest {

    @Test
    void shouldReturnAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Task"));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Task\", \"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Task"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNotFound());
    }


    @ParameterizedTest
    @MethodSource("provideTasksForTesting")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldValidateTaskNameWhenUpdate(String name, boolean shouldPass) throws Exception {
        String taskJson = "{\"name\":\"" + name + "\",\"completed\":false}";

        if (!shouldPass) {
            mockMvc.perform(put("/api/tasks/" + task.getId())
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
