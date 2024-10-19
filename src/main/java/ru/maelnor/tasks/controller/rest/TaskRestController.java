package ru.maelnor.tasks.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.dto.ErrorResponse;
import ru.maelnor.tasks.dto.filter.TaskFilter;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.service.TaskService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API для управления задачами")
public class TaskRestController {

    private final TaskService taskService;

    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка задач",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskModel> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks.stream().map(TaskMapper.INSTANCE::toDto).toList());
    }

    @Operation(summary = "Получить задачи с фильтрацией и пагинацией", description = "Возвращает страницу задач с фильтрацией и пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение страницы задач", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/filtered")
    public Page<TaskDto> getFilteredTasks(@Valid TaskFilter taskFilter) {
        return taskService.filterBy(taskFilter).map(TaskMapper.INSTANCE::toDto);
    }


    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<TaskDto> getTaskById(@Parameter(description = "ID задачи", required = true) @PathVariable UUID id) {
        return taskService.getTaskById(id)
                .map(TaskMapper.INSTANCE::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу и возвращает её данные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = TaskMapper.INSTANCE.toDto(taskService.addTask(taskDto));
        return ResponseEntity.ok(createdTask);
    }

    @Operation(summary = "Обновить существующую задачу", description = "Обновляет задачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TaskDto> updateTask(@Parameter(description = "ID задачи", required = true) @PathVariable UUID id,
                                              @Valid @RequestBody TaskDto taskDto) {
        Optional<TaskDto> updatedTask = taskService.getTaskById(id)
                .map(TaskMapper.INSTANCE::toDto)
                .map(existingTask -> {
                    existingTask.setName(taskDto.getName());
                    existingTask.setCompleted(taskDto.isCompleted());
                    taskService.updateTask(existingTask);
                    return existingTask;
                });
        return updatedTask.map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<TaskDto> deleteTask(@Parameter(description = "ID задачи", required = true) @PathVariable UUID id, @AuthenticationPrincipal UserEntity currentUser) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
