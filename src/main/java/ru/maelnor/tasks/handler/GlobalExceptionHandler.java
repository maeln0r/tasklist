package ru.maelnor.tasks.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всех контроллеров.
 * Обрабатывает различные типы исключений и возвращает структурированный ответ с информацией об ошибке.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает все типы исключений и возвращает ответ с кодом 500 (Internal Server Error).
     *
     * @param ex      исключение, которое было выброшено
     * @param request информация о веб-запросе, который вызвал исключение
     * @return объект {@link ResponseEntity} с телом, содержащим статус ошибки, сообщение и путь запроса
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает исключения типа {@link RuntimeException} и возвращает ответ с кодом 500 (Internal Server Error).
     *
     * @param ex      исключение, которое было выброшено
     * @param request информация о веб-запросе, который вызвал исключение
     * @return объект {@link ResponseEntity} с телом, содержащим статус ошибки, сообщение и путь запроса
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Runtime Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
