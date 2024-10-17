package ru.maelnor.tasks.controller.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.maelnor.tasks.dto.ErrorResponse;
import ru.maelnor.tasks.exception.AlreadyExistsException;
import ru.maelnor.tasks.exception.RefreshTokenException;
import ru.maelnor.tasks.exception.TaskNotFoundException;

import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Hidden
public class ExceptionHandlerController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)))
    })
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> taskNotFoundException(TaskNotFoundException e) {
        UUID uuid = UUID.randomUUID();
        log.error("{} - Ошибка при попытке получить задачу.", uuid, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getLocalizedMessage(), HttpStatus.NOT_FOUND.value(), uuid));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей задачи", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)))
    })
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        UUID uuid = UUID.randomUUID();
        log.error("{} - Ошибка валидации полей задачи.", uuid, e);

        BindingResult bindingResult = e.getBindingResult();
        String errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> String.format("Поле '%s': %s (Значение: '%s')",
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()))
                .collect(Collectors.joining("\n"));

        // Включение глобальных ошибок (если есть)
        String globalErrors = bindingResult.getGlobalErrors()
                .stream()
                .map(globalError -> String.format("Общая ошибка: %s", globalError.getDefaultMessage()))
                .collect(Collectors.joining("\n"));

        if (!globalErrors.isEmpty()) {
            errors += "\n" + globalErrors;
        }

        return ResponseEntity.badRequest().body(new ErrorResponse(errors, HttpStatus.BAD_REQUEST.value(), uuid));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Ошибка конвертации JSON", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)))
    })
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> taskArgumentNotValidException(HttpMessageNotReadableException e) {
        UUID uuid = UUID.randomUUID();
        log.error("{} - Ошибка конвертации JSON", uuid, e);
        String errors = "Невозможно прочитать JSON или произошла ошибка валидации.";
        return ResponseEntity.badRequest().body(new ErrorResponse(errors, HttpStatus.BAD_REQUEST.value(), uuid));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Ошибка обновления токена", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)))
    })
    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> refreshTokenException(RefreshTokenException e) {
        UUID uuid = UUID.randomUUID();
        log.error("{} - Ошибка обновления токена", uuid, e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), uuid));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Ошибка добавления токена", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)))
    })
    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> alreadyExistsException(RefreshTokenException e) {
        UUID uuid = UUID.randomUUID();
        log.error("{} - Ошибка добавления токена", uuid, e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), uuid));
    }
}
