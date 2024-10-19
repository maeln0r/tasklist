package ru.maelnor.tasks.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private UUID id;
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private List<String> roles;
}
