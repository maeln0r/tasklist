package ru.maelnor.tasks.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.maelnor.tasks.exception.UserNotFoundException;
import ru.maelnor.tasks.model.UserModel;
import ru.maelnor.tasks.service.UserService;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER') or @userProfilePermissionEvaluator.isProfileOwner(#id)")
    public String showUserProfile(@PathVariable UUID id, Model model, HttpServletRequest request) {
        UserModel user = userService.getUserById(id).orElseThrow(() -> new UserNotFoundException(id));

        model.addAttribute("user", user);
        model.addAttribute("request", request);
        model.addAttribute("pageTitle", "Профиль пользователя " + user.getUsername());

        return "user";
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userProfilePermissionEvaluator.isProfileOwner(#userId)")
    public String changePassword(@RequestParam("user_id") UUID userId, @RequestParam("password") String password, Model model) {
        Optional<UserModel> user = userService.getUserById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        userService.changeUserPassword(userId, password);

        return MessageFormat.format("redirect:/user/profile/{0}?passwordChanged=true", userId);
    }
}

