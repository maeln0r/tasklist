package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.security.AppUserDetails;
import ru.maelnor.tasks.security.CustomUserDetails;

/**
 * Сервис для получения информации о текущем аутентифицированном пользователе.
 * Определяет, находится ли пользователь в системе через OIDC или классические данные.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final OidcUserService userService;

    /**
     * Возвращает информацию о текущем аутентифицированном пользователе.
     * Проверяет тип пользователя, будь то {@link AppUserDetails} или {@link DefaultOidcUser}, и возвращает соответствующую реализацию.
     *
     * @return объект {@link CustomUserDetails}, содержащий информацию о текущем пользователе
     * @throws IllegalStateException если тип principal не распознан
     */
    public CustomUserDetails getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication.getPrincipal() instanceof AppUserDetails) {
            return (AppUserDetails) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser oidcUser) {
            return userService.createOidcAppUserDetails(oidcUser);
        }

        throw new IllegalStateException("Unknown authentication principal type");
    }
}
