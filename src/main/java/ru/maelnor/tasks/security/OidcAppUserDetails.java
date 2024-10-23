package ru.maelnor.tasks.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ru.maelnor.tasks.entity.RoleType;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Реализация пользовательских деталей для OIDC аутентифицированных пользователей.
 * Предоставляет информацию о пользователе, а также проверяет его роли.
 */
@RequiredArgsConstructor
public class OidcAppUserDetails implements CustomUserDetails {

    private final OidcUser oidcUser;
    private final Set<RoleType> roles;

    /**
     * Возвращает идентификатор пользователя, полученный из OIDC.
     *
     * @return идентификатор пользователя {@link UUID}
     */
    @Override
    public UUID getId() {
        return UUID.fromString(oidcUser.getSubject());
    }

    /**
     * Возвращает email пользователя, полученный из OIDC.
     *
     * @return email пользователя
     */
    @Override
    public String getEmail() {
        return oidcUser.getEmail();
    }

    /**
     * Возвращает имя пользователя, полученное из OIDC.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return oidcUser.getPreferredUsername();
    }

    /**
     * Возвращает коллекцию прав пользователя, полученную из OIDC.
     *
     * @return коллекция {@link GrantedAuthority} с правами пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    /**
     * Проверяет, имеет ли пользователь роль администратора.
     *
     * @return {@code true}, если пользователь имеет роль администратора
     */
    @Override
    public boolean isAdmin() {
        return roles.contains(RoleType.ROLE_ADMIN);
    }

    /**
     * Проверяет, имеет ли пользователь роль пользователя.
     *
     * @return {@code true}, если пользователь имеет роль пользователя
     */
    @Override
    public boolean isUser() {
        return roles.contains(RoleType.ROLE_USER);
    }

    /**
     * Проверяет, имеет ли пользователь роль менеджера.
     *
     * @return {@code true}, если пользователь имеет роль менеджера
     */
    @Override
    public boolean isManager() {
        return roles.contains(RoleType.ROLE_MANAGER);
    }
}
