package ru.maelnor.tasks.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.service.OidcUserService;

import java.io.IOException;

/**
 * Обработчик успешной аутентификации, настраиваемый для обработки OIDC пользователей.
 * После успешной аутентификации пользователя через OIDC создает или обновляет запись пользователя в базе данных
 * и перенаправляет на страницу задач.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OidcUserService userService;

    /**
     * Метод, вызываемый при успешной аутентификации.
     * Если пользователь аутентифицирован через OIDC, создается или обновляется запись пользователя в базе данных.
     *
     * @param request        текущий HTTP-запрос
     * @param response       текущий HTTP-ответ
     * @param authentication объект аутентификации, содержащий информацию о пользователе
     * @throws IOException      если происходит ошибка ввода-вывода
     * @throws ServletException если происходит ошибка сервлета
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            // Поиск или создание пользователя на основе данных OIDC
            UserEntity userEntity = userService.findOrCreateUserFromOidc(oidcUser);
            userService.addOrUpdateUser(userEntity);
        }
        // Перенаправление пользователя на страницу задач
        response.sendRedirect("/tasks");
    }
}
