package ru.maelnor.tasks.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик, который вызывается, когда неаутентифицированный пользователь пытается получить доступ к защищенному ресурсу.
 * Реализует интерфейс {@link AuthenticationEntryPoint} и отправляет JSON-ответ с кодом ошибки 401 (Unauthorized).
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Метод, который вызывается при попытке неаутентифицированного доступа к защищенному ресурсу.
     * Логирует ошибку и отправляет JSON-ответ с кодом ошибки 401 и дополнительной информацией.
     *
     * @param request  объект {@link HttpServletRequest}, содержащий данные запроса
     * @param response объект {@link HttpServletResponse}, содержащий данные ответа
     * @param authException исключение, вызванное неудачной аутентификацией
     * @throws IOException если возникает ошибка ввода-вывода при обработке ответа
     * @throws ServletException если возникает ошибка в сервлете
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
        mapper.writeValue(response.getOutputStream(), body);
    }
}
