package ru.maelnor.tasks.service;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.security.AppUserDetails;

@Service
public class CurrentUserService {
    public AppUserDetails getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        return (AppUserDetails) context.getAuthentication().getPrincipal();
    }
}