package ru.maelnor.tasks.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.maelnor.tasks.service.CurrentUserService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
private final CurrentUserService currentUser;
    @ModelAttribute
    public void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = currentUser.getCurrentUser().getUsername();
            model.addAttribute("username", username);
        }
    }
}
