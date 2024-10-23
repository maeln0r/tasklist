package ru.maelnor.tasks.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.maelnor.tasks.security.AppUserDetails;

import java.time.Duration;
import java.util.Date;

/**
 * Утилитный класс для работы с JWT токенами.
 * Позволяет генерировать, извлекать информацию и валидировать JWT токены.
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    /**
     * Генерирует JWT токен на основе данных пользователя.
     *
     * @param userDetails объект с деталями пользователя {@link AppUserDetails}
     * @return сгенерированный JWT токен
     */
    public String generateToken(AppUserDetails userDetails) {
        return generateTokenFromUserName(userDetails.getUsername());
    }

    /**
     * Генерирует JWT токен на основе имени пользователя.
     *
     * @param username имя пользователя
     * @return сгенерированный JWT токен
     */
    public String generateTokenFromUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT токена.
     *
     * @param token JWT токен
     * @return имя пользователя, извлеченное из токена
     */
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Валидирует JWT токен. Проверяет его подпись, формат и срок действия.
     *
     * @param token JWT токен
     * @return {@code true}, если токен валидный, иначе {@code false}
     */
    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
