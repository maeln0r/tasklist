package ru.maelnor.tasks.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * Пользовательский интерфейс для представления данных пользователя в системе безопасности.
 * Содержит методы для получения идентификатора, имени, email и ролей пользователя, а также для проверки ролей.
 */
public interface CustomUserDetails {

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя {@link UUID}
     */
    UUID getId();

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    String getUsername();

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    String getEmail();

    /**
     * Возвращает коллекцию ролей пользователя в виде объектов {@link GrantedAuthority}.
     *
     * @return коллекция прав пользователя
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * Проверяет, имеет ли пользователь роль администратора.
     *
     * @return {@code true}, если пользователь имеет роль администратора
     */
    boolean isAdmin();

    /**
     * Проверяет, имеет ли пользователь роль пользователя.
     *
     * @return {@code true}, если пользователь имеет роль пользователя
     */
    boolean isUser();

    /**
     * Проверяет, имеет ли пользователь роль менеджера.
     *
     * @return {@code true}, если пользователь имеет роль менеджера
     */
    boolean isManager();
}
