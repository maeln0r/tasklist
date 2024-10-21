package ru.maelnor.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.security.AppUserDetails;
import ru.maelnor.tasks.security.CustomUserDetails;

import java.util.Map;

@Service
public class CurrentUserService {
    @Autowired
    UserService userService;

    public CustomUserDetails getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication.getPrincipal() instanceof AppUserDetails) {
            return (AppUserDetails) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            Map<String, Object> map = oidcUser.getClaimAsMap("realm_access");
            return userService.createOidcAppUserDetails(oidcUser);
        }

        throw new IllegalStateException("Unknown authentication principal type");
    }
}
