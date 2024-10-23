package ru.maelnor.tasks.model;

import lombok.*;
import ru.maelnor.tasks.entity.RoleType;

import java.util.Set;

/**
 * Модель запроса для создания нового пользователя.
 * Содержит данные, необходимые для регистрации нового пользователя, включая имя пользователя, email, роли и пароль.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
    private String username;
    private String email;
    private Set<RoleType> roles;
    private String password;
}
