package ru.maelnor.tasks.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    public String showUserProfile(@PathVariable UUID id, Model model, HttpServletRequest request) {
        Optional<UserModel> user = userService.getUserById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        model.addAttribute("user", user.get());
        model.addAttribute("request", request);
        model.addAttribute("pageTitle", "Профиль пользователя " + user.get().getUsername());

        return "user";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("user_id") UUID userId, @RequestParam("password") String password, Model model) {
        Optional<UserModel> user = userService.getUserById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        userService.changeUserPassword(userId, password);

        return MessageFormat.format("redirect:/user/profile/{0}?passwordChanged=true", userId);
    }
}

