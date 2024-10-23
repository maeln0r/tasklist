package ru.maelnor.tasks.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.maelnor.tasks.service.CurrentUserService;

import java.util.UUID;

/**
 * Класс, предоставляющий глобальные данные для всех контроллеров.
 * Добавляет информацию о текущем пользователе в модель для использования в шаблонах представления.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CurrentUserService currentUser;

    /**
     * Метод, который добавляет информацию о текущем пользователе в модель,
     * если пользователь аутентифицирован.
     *
     * @param model объект модели, куда будет добавлена информация о пользователе
     */
    @ModelAttribute
    public void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            String username = currentUser.getCurrentUser().getUsername();
            UUID user_id = currentUser.getCurrentUser().getId();
            model.addAttribute("username", username); // Добавление имени пользователя в модель
            model.addAttribute("user_id", user_id); // Добавление id пользователя в модель
        }
    }
}
