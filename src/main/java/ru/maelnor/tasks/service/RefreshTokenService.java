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

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return jpaRefreshTokenRepository.findByRefreshToken(token);
    }

    public RefreshTokenEntity createRefreshToken(Long userId) {
        var refreshToken = RefreshTokenEntity
                .builder()
                .userId(userId)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration.toMillis()))
                .refreshToken(UUID.randomUUID().toString())
                .build();
        refreshToken = jpaRefreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshTokenEntity checkRefreshToken(RefreshTokenEntity token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            jpaRefreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getRefreshToken(), "Токен просрочен");
        }
        return token;
    }

    public void deleteByUserId(Long userId) {
        jpaRefreshTokenRepository.deleteByUserId(userId);
    }
}
