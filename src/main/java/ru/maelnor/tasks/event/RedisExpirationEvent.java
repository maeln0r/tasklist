package ru.maelnor.tasks.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;
import ru.maelnor.tasks.entity.RefreshTokenEntity;

@Component
@Slf4j
public class RedisExpirationEvent {
    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<RefreshTokenEntity> event) {
        RefreshTokenEntity refreshToken = (RefreshTokenEntity) event.getValue();
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is null in handleRedisKeyExpiredEvent function");
        }
        log.info("Refresh token with key={} has expired. Refrash token is: {}", refreshToken.getId(), refreshToken.getRefreshToken());
    }
}
