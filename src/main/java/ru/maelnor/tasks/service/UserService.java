package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.entity.RoleType;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.repository.JpaUserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;

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
