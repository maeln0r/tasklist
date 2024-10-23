package ru.maelnor.tasks.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

/**
 * Сущность для хранения информации о refresh токенах в Redis.
 * Каждый токен содержит идентификатор пользователя, сам токен, время его истечения и уникальный идентификатор.
 */
@RedisHash("refresh_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenEntity {
    @Id
    @Indexed
    private Long id;
    @Indexed
    private UUID userId;
    @Indexed
    private String refreshToken;
    @Indexed
    private Instant expiresAt;
}
