package ru.maelnor.tasks.model;

import lombok.*;
import ru.maelnor.tasks.entity.RoleType;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
    private String username;
    private String email;
    private Set<RoleType> roles;
    private String password;
}
