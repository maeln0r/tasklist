package ru.maelnor.tasks.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ru.maelnor.tasks.entity.RoleType;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class OidcAppUserDetails implements CustomUserDetails {

    private final OidcUser oidcUser;
    private final Set<RoleType> roles;


    @Override
    public UUID getId() {
        return UUID.fromString(oidcUser.getSubject());
    }

    @Override
    public String getEmail() {
        return oidcUser.getEmail();
    }

    @Override
    public String getUsername() {
        return oidcUser.getPreferredUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public boolean isAdmin() {
        return roles.contains(RoleType.ROLE_ADMIN);
    }

    @Override
    public boolean isUser() {
        return roles.contains(RoleType.ROLE_USER);
    }

    @Override
    public boolean isManager() {
        return roles.contains(RoleType.ROLE_MANAGER);
    }
}
