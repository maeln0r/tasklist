package ru.maelnor.tasks.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.repository.JpaUserRepository;

import java.text.MessageFormat;

/**
 * Реализация {@link UserDetailsService}, которая загружает данные пользователя из базы данных.
 * Используется для аутентификации пользователей в Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserRepository userRepository;

    /**
     * Загружает данные пользователя по имени пользователя (username).
     *
     * @param username имя пользователя для поиска
     * @return объект {@link UserDetails}, содержащий информацию о пользователе
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("Пользователь {0} не найден", username)));
        return new AppUserDetails(user);
    }
}
