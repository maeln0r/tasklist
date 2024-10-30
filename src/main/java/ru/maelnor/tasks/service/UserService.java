package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.mapper.UserMapper;
import ru.maelnor.tasks.model.UserModel;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.security.CustomUserDetails;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления локальными пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;


    /**
     * Получить модель пользователя по ID с проверкой прав
     *
     * @param id - id пользователя
     * @return - модель пользователя
     */
    public Optional<UserModel> getUserById(UUID id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        if (userEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = userEntityOptional.get();
        CustomUserDetails currentUser = currentUserService.getCurrentUser();

        if (currentUser.isAdmin() || currentUser.isManager() || userEntity.getId().equals(currentUser.getId())) {
            return Optional.of(UserMapper.INSTANCE.toModel(userEntity));
        } else {
            throw new AccessDeniedException("Недостаточно прав для просмотра профиля пользователя");
        }
    }

    /**
     * Сменить локальный пароль пользователя (с проверкой прав на операцию)
     *
     * @param id       - id пользователя
     * @param password - новый пароль
     */
    public void changeUserPassword(UUID id, String password) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            CustomUserDetails currentUser = currentUserService.getCurrentUser();

            if (currentUser.isAdmin() || userEntity.getId().equals(currentUser.getId())) {
                userEntity.setPassword(passwordEncoder.encode(password));
                userRepository.save(userEntity);
            } else {
                throw new AccessDeniedException("Недостаточно прав для изменения пароля");
            }
        }
    }

    public Map<String, String> getUsesWithTask() {
        return userRepository.findUsersWithTasks().stream()
                .collect(Collectors.toMap(UserEntity::getUsername, user -> user.getId().toString()));
    }
}
