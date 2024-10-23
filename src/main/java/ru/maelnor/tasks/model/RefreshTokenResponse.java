package ru.maelnor.tasks.model;

import lombok.*;

/**
 * Модель ответа при обновлении токена.
 * Содержит новый access токен и refresh токен.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
