package ru.maelnor.tasks.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Модель ответа при аутентификации пользователя.
 * Содержит информацию о пользователе, токене доступа, refresh токене и ролях пользователя.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private UUID id;
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private List<String> roles;
}
