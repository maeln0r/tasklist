package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.RoleType;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.mapper.UserMapper;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.model.UserModel;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.security.CustomUserDetails;
import ru.maelnor.tasks.security.OidcAppUserDetails;

import java.util.*;

/**
 * Сервис для управления локальными пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;



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
}
