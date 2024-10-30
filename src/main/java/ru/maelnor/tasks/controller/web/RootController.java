package ru.maelnor.tasks.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Базовый контроллер для редиректа
 */
@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToTasks() {
        return "redirect:/tasks";
    }
}
