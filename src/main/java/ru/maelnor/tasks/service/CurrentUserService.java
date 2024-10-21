package ru.maelnor.tasks.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.security.AppUserDetails;
import ru.maelnor.tasks.security.CustomUserDetails;
import ru.maelnor.tasks.security.OidcAppUserDetails;

import java.util.Map;

@Service
public class CurrentUserService {

    public CustomUserDetails getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication.getPrincipal() instanceof AppUserDetails) {
            // Локальный пользователь
            return (AppUserDetails) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            // Пользователь аутентифицирован через OIDC
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            Map<String, Object> map = oidcUser.getClaimAsMap("realm_access");
            return new OidcAppUserDetails(oidcUser);
        }

        throw new IllegalStateException("Unknown authentication principal type");
    }
}
