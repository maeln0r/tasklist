package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.RoleType;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.security.OidcAppUserDetails;

import java.util.*;

/**
 * Сервис для управления пользователями, включая создание, обновление и преобразование OIDC-пользователей.
 * Обеспечивает взаимодействие с базой данных пользователей и преобразование ролей пользователей.
 */
@Service
@RequiredArgsConstructor
public class OidcUserService {
    private final JpaUserRepository userRepository;

    /**
     * Добавляет или обновляет данные пользователя в базе данных.
     *
     * @param userEntity сущность пользователя {@link UserEntity}, которая будет добавлена или обновлена
     */
    public void addOrUpdateUser(UserEntity userEntity) {
        Optional<UserEntity> existingUser = userRepository.findById(userEntity.getId());

        if (existingUser.isPresent()) {
            UserEntity userToUpdate = existingUser.get();
            userToUpdate.setUsername(userEntity.getUsername());
            userToUpdate.setEmail(userEntity.getEmail());
            userToUpdate.setRoles(userEntity.getRoles());
            userRepository.save(userToUpdate);
        } else {
            userRepository.save(userEntity);
        }
    }

    /**
     * Находит пользователя по OIDC или создает нового, если он не существует.
     *
     * @param oidcUser объект OIDC-пользователя {@link OidcUser}, из которого извлекаются данные
     * @return сущность пользователя {@link UserEntity}, существующая или созданная
     */
    public UserEntity findOrCreateUserFromOidc(OidcUser oidcUser) {
        String username = oidcUser.getPreferredUsername();

        Optional<UserEntity> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            UserEntity newUser = UserEntity.builder()
                    .email(oidcUser.getEmail())
                    .username(oidcUser.getPreferredUsername())
                    .id(UUID.fromString(oidcUser.getSubject()))
                    .build();

            newUser.setRoles(convertOidcRolesToRoleType(oidcUser));
            return newUser;
        }
    }

    /**
     * Создает объект пользовательских деталей для OIDC пользователя.
     *
     * @param oidcUser объект OIDC-пользователя {@link OidcUser}
     * @return объект {@link OidcAppUserDetails}, содержащий данные и роли пользователя
     */
    public OidcAppUserDetails createOidcAppUserDetails(OidcUser oidcUser) {
        Set<RoleType> roles = convertOidcRolesToRoleType(oidcUser);
        return new OidcAppUserDetails(oidcUser, roles);
    }

    /**
     * Преобразует OIDC роли пользователя в роли типа {@link RoleType}.
     *
     * @param oidcUser объект OIDC-пользователя {@link OidcUser}
     * @return набор ролей пользователя {@link Set} типа {@link RoleType}
     */
    private Set<RoleType> convertOidcRolesToRoleType(OidcUser oidcUser) {
        Set<RoleType> roles = new HashSet<>();

        if (oidcUser.getClaimAsMap("realm_access") != null) {
            var realmAccess = oidcUser.getClaimAsMap("realm_access");
            if (realmAccess.containsKey("roles")) {
                var oidcRoles = (List<String>) realmAccess.get("roles");

                for (String role : oidcRoles) {
                    switch (role) {
                        case "ADMIN":
                            roles.add(RoleType.ROLE_ADMIN);
                            break;
                        case "MANAGER":
                            roles.add(RoleType.ROLE_MANAGER);
                            break;
                        case "USER":
                        default:
                            roles.add(RoleType.ROLE_USER);
                            break;
                    }
                }
            }
        }

        return roles;
    }
}
