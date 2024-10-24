package ru.maelnor.tasks.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Сущность для хранения данных о пользователях в базе данных.
 * Содержит информацию о пользователе, включая имя, пароль, email, и набор ролей.
 */
@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    private UUID id;
    private String username;
    private String password;
    private String email;

    /**
     * Набор ролей пользователя. Роли хранятся в отдельной таблице "user_roles"
     * и связаны с пользователем через его идентификатор.
     */
    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RoleType> roles = new HashSet<>();
}
