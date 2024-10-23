package ru.maelnor.tasks.model;

import lombok.*;

/**
 * Модель запроса для обновления access токена с использованием refresh токена.
 * Содержит только поле для refresh токена.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequest {
    private String refreshToken;
}
