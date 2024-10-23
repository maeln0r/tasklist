package ru.maelnor.tasks.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;
import ru.maelnor.tasks.entity.RefreshTokenEntity;

/**
 * Компонент для обработки событий истечения ключей Redis.
 * Слушает события истечения срока действия ключей, связанных с {@link RefreshTokenEntity}.
 */
@Component
@Slf4j
public class RedisExpirationEvent {

    /**
     * Обрабатывает событие истечения ключа Redis, связанного с {@link RefreshTokenEntity}.
     * Если истекший ключ относится к refresh токену, информация об этом выводится в лог.
     *
     * @param event событие истечения ключа Redis
     */
    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<RefreshTokenEntity> event) {
        RefreshTokenEntity refreshToken = (RefreshTokenEntity) event.getValue();
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is null in handleRedisKeyExpiredEvent function");
        }
        log.info("Refresh token with key={} has expired. Refresh token is: {}", refreshToken.getId(), refreshToken.getRefreshToken());
    }
}
