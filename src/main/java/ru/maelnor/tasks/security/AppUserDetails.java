package ru.maelnor.tasks.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.maelnor.tasks.entity.RoleType;
import ru.maelnor.tasks.entity.UserEntity;

import java.util.Collection;
import java.util.UUID;

/**
 * Реализация интерфейса {@link UserDetails} для предоставления данных о пользователе,
 * используемых в процессе аутентификации и авторизации.
 * Содержит данные пользователя из сущности {@link UserEntity}.
 */
@RequiredArgsConstructor
public class AppUserDetails implements UserDetails, CustomUserDetails {

    private final UserEntity user;

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя {@link UUID}
     */
    @Override
    public UUID getId() {
        return user.getId();
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    @Override
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * Возвращает коллекцию ролей пользователя, преобразованных в {@link GrantedAuthority}.
     *
     * @return коллекция прав пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user
                .getRoles()
                .stream()
                .map(roleType -> new SimpleGrantedAuthority(roleType.name()))
                .toList();
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Указывает, что учетная запись пользователя не истекла.
     *
     * @return {@code true}, если учетная запись активна
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, что учетная запись пользователя не заблокирована.
     *
     * @return {@code true}, если учетная запись не заблокирована
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, что учетные данные пользователя не истекли.
     *
     * @return {@code true}, если учетные данные действительны
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, что учетная запись пользователя включена.
     *
     * @return {@code true}, если учетная запись активна
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Проверяет, является ли пользователь с ролью {@link RoleType#ROLE_USER}.
     *
     * @return {@code true}, если пользователь имеет роль ROLE_USER
     */
    @Override
    public boolean isUser() {
        return user.getRoles().contains(RoleType.ROLE_USER);
    }

    /**
     * Проверяет, является ли пользователь с ролью {@link RoleType#ROLE_ADMIN}.
     *
     * @return {@code true}, если пользователь имеет роль ROLE_ADMIN
     */
    @Override
    public boolean isAdmin() {
        return user.getRoles().contains(RoleType.ROLE_ADMIN);
    }

    /**
     * Проверяет, является ли пользователь с ролью {@link RoleType#ROLE_MANAGER}.
     *
     * @return {@code true}, если пользователь имеет роль ROLE_MANAGER
     */
    @Override
    public boolean isManager() {
        return user.getRoles().contains(RoleType.ROLE_MANAGER);
    }
}
