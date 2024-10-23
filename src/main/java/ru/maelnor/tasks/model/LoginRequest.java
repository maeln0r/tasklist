package ru.maelnor.tasks.model;

import lombok.*;

/**
 * Модель запроса для выполнения входа в систему.
 * Содержит данные, необходимые для аутентификации пользователя, такие как имя пользователя и пароль.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    private String username;
    private String password;
}
