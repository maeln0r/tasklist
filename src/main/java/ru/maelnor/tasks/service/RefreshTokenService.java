package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.RefreshTokenEntity;
import ru.maelnor.tasks.exception.RefreshTokenException;
import ru.maelnor.tasks.repository.JpaRefreshTokenRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления refresh токенами.
 * Обрабатывает создание, проверку и удаление refresh токенов в системе.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    /**
     * Находит refresh токен по его значению.
     *
     * @param token значение refresh токена
     * @return объект {@link Optional}, содержащий найденный токен, если он существует
     */
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return jpaRefreshTokenRepository.findByRefreshToken(token);
    }

    /**
     * Создает новый refresh токен для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return созданный refresh токен {@link RefreshTokenEntity}
     */
    public RefreshTokenEntity createRefreshToken(UUID userId) {
        var refreshToken = RefreshTokenEntity
                .builder()
                .userId(userId)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration.toMillis()))
                .refreshToken(UUID.randomUUID().toString())
                .build();
        refreshToken = jpaRefreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    /**
     * Проверяет, не истек ли срок действия refresh токена.
     * Если срок истек, токен удаляется, и выбрасывается исключение {@link RefreshTokenException}.
     *
     * @param token объект {@link RefreshTokenEntity}, представляющий refresh токен
     * @return проверенный refresh токен, если он действителен
     * @throws RefreshTokenException если токен просрочен
     */
    public RefreshTokenEntity checkRefreshToken(RefreshTokenEntity token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            jpaRefreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getRefreshToken(), "Токен просрочен");
        }
        return token;
    }

    /**
     * Удаляет все refresh токены, связанные с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     */
    public void deleteByUserId(UUID userId) {
        jpaRefreshTokenRepository.deleteByUserId(userId);
    }
}
