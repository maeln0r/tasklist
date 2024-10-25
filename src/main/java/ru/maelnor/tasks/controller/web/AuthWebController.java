package ru.maelnor.tasks.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.maelnor.tasks.model.AuthResponse;
import ru.maelnor.tasks.model.LoginRequest;
import ru.maelnor.tasks.security.SecurityService;

@Controller
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth-type", havingValue = "jwt")
public class AuthWebController {
    private final SecurityService securityService;

    @GetMapping("/login")
    public String loginPage(Model model,
                            @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", error != null);
        return "login";
    }

    @PostMapping("/perform_login")
    public String performLogin(@RequestBody LoginRequest loginRequest, Model model) {
        try {
            // Аутентифицируем пользователя с помощью SecurityService
            AuthResponse authResponse = securityService.authenticate(loginRequest);

            // Переход на защищённую страницу после успешного входа
            return "redirect:/tasks";
        } catch (Exception e) {
            // Если ошибка аутентификации, перенаправляем обратно на страницу логина с сообщением об ошибке
            model.addAttribute("error", "Неверное имя пользователя или пароль?");
            return "login";
        }
    }
}

