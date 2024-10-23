package ru.maelnor.tasks.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.RefreshTokenEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.exception.RefreshTokenException;
import ru.maelnor.tasks.model.*;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.security.jwt.JwtUtils;
import ru.maelnor.tasks.service.RefreshTokenService;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления аутентификацией, регистрацией, обновлением токенов и выходом пользователя.
 * Работает с JWT токенами и refresh токенами для управления безопасностью.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth-type", havingValue = "jwt")
public class SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Аутентифицирует пользователя на основе данных из запроса на вход и возвращает JWT токен и refresh токен.
     *
     * @param loginRequest запрос на вход {@link LoginRequest}, содержащий имя пользователя и пароль
     * @return объект {@link AuthResponse}, содержащий токены и информацию о пользователе
     */
    public AuthResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return AuthResponse.builder()
                .id(userDetails.getId())
                .token(jwtUtils.generateToken(userDetails))
                .refreshToken(refreshToken.getRefreshToken())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param createUserRequest запрос на создание пользователя {@link CreateUserRequest}
     * @deprecated Этот метод устарел, рекомендуется использовать более современные подходы к регистрации.
     */
    @Deprecated
    public void register(CreateUserRequest createUserRequest) {
        var user = UserEntity.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .email(createUserRequest.getEmail())
                .build();

        user.setRoles(createUserRequest.getRoles());
        userRepository.save(user);
    }

    /**
     * Обновляет access токен с использованием refresh токена.
     *
     * @param refreshTokenRequest запрос на обновление токена {@link RefreshTokenRequest}
     * @return объект {@link RefreshTokenResponse}, содержащий новый access токен и refresh токен
     */
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshTokenEntity::getUserId)
                .map(userId -> {
                    UserEntity tokenOwner = userRepository.findById(userId).orElseThrow(()
                            -> new RefreshTokenException(MessageFormat.format("Ошибка получения токена для userId: {0}", userId)));
                    String token = jwtUtils.generateTokenFromUserName(tokenOwner.getUsername());
                    return new RefreshTokenResponse(token, refreshTokenService.createRefreshToken(userId).getRefreshToken());
                }).orElseThrow(() -> new RefreshTokenException(refreshToken, "Refresh token не найден"));
    }

    /**
     * Выполняет выход пользователя, удаляя его refresh токены.
     */
    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentPrincipal instanceof AppUserDetails userDetails) {
            UUID userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
        }
    }
}
