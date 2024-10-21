package ru.maelnor.tasks.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public interface CustomUserDetails {
    UUID getId();

    String getUsername();

    String getEmail();

    Collection<? extends GrantedAuthority> getAuthorities();

    boolean isAdmin();

    boolean isUser();

    boolean isManager();

}

