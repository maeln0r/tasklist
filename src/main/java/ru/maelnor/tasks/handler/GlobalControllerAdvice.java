package ru.maelnor.tasks.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.maelnor.tasks.service.CurrentUserService;

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
        if (authentication != null && authentication.isAuthenticated()) {
            String username = currentUser.getCurrentUser().getUsername();
            model.addAttribute("username", username); // Добавление имени пользователя в модель
        }
    }
}
