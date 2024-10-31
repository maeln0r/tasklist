package ru.maelnor.tasks.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всех контроллеров.
 * Обрабатывает различные типы исключений и возвращает структурированный ответ с информацией об ошибке.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает все типы исключений. Возвращает JSON для REST-запросов и HTML для обычных запросов.
     *
     * @param ex      исключение, которое было выброшено
     * @param request информация о веб-запросе, который вызвал исключение
     * @return JSON-ответ для REST-запросов или HTML-страница для обычных запросов
     */
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, WebRequest request) {
        if (isRestRequest(request)) {
            return buildJsonResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return buildHtmlResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает исключения типа {@link RuntimeException}.
     *
     * @param ex      исключение, которое было выброшено
     * @param request информация о веб-запросе, который вызвал исключение
     * @return JSON-ответ для REST-запросов или HTML-страница для обычных запросов
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex, WebRequest request) {
        if (isRestRequest(request)) {
            return buildJsonResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return buildHtmlResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Проверяет, является ли запрос REST-запросом (JSON).
     */
    private boolean isRestRequest(WebRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("application/json");
    }

    /**
     * Создает JSON-ответ для REST-запросов.
     */
    private ResponseEntity<Object> buildJsonResponse(Exception ex, WebRequest request, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }

    /**
     * Создает HTML-ответ для обычных запросов.
     */
    private ModelAndView buildHtmlResponse(Exception ex, HttpStatus status) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("error", status.getReasonPhrase());
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.setStatus(status);
        return modelAndView;
    }
}
