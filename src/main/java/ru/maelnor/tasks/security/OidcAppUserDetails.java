package ru.maelnor.tasks.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.UUID;

public class OidcAppUserDetails implements CustomUserDetails {

    private final OidcUser oidcUser;
    private final String rolePrefix = "OIDC_";

    public OidcAppUserDetails(OidcUser oidcUser) {
        this.oidcUser = oidcUser;
    }

    @Override
    public UUID getId() {
        // В OIDC может не быть прямого UUID, можно взять уникальный идентификатор из токена
        return UUID.fromString(oidcUser.getSubject());
    }

    @Override
    public String getEmail() {
        return oidcUser.getEmail();
    }

    @Override
    public String getUsername() {
        return oidcUser.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public boolean isAdmin() {
        // Проверяйте роли, если они приходят в токене или загружаются из Keycloak
        return oidcUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(rolePrefix + "ADMIN"));
    }

    @Override
    public boolean isUser() {
        return oidcUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(rolePrefix + "USER"));
    }

    @Override
    public boolean isManager() {
        return oidcUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(rolePrefix + "MANAGER"));
    }
}
