package ru.maelnor.tasks.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик для ошибок отказа в доступе (403 Forbidden) при работе с JWT.
 * Реализует интерфейс {@link AccessDeniedHandler} и отправляет JSON-ответ при отказе в доступе.
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Метод для обработки отказа в доступе.
     * Логирует ошибку и отправляет JSON-ответ с кодом ошибки 403 и дополнительной информацией.
     *
     * @param request  объект {@link HttpServletRequest}, содержащий данные запроса
     * @param response объект {@link HttpServletResponse}, содержащий данные ответа
     * @param accessDeniedException исключение, вызванное отказом в доступе
     * @throws IOException если возникает ошибка ввода-вывода при обработке ответа
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("Access denied error: {}", accessDeniedException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("error", "Forbidden");
        body.put("message", accessDeniedException.getMessage());
        body.put("path", request.getServletPath());
        mapper.writeValue(response.getOutputStream(), body);
    }
}
