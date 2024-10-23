package ru.maelnor.tasks.controller.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.dto.filter.TaskFilter;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.service.TaskService;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Веб-контроллер для управления задачами. Обрабатывает запросы для отображения, создания, редактирования и удаления задач.
 * Использует шаблоны представления для отображения страниц.
 */
@Controller
@RequestMapping("/tasks")
public class TaskWebController {

    private final TaskService taskService;

    /**
     * Конструктор для внедрения сервиса управления задачами.
     *
     * @param taskService сервис для работы с задачами
     */
    public TaskWebController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Инициализирует разрешенные поля для привязки данных.
     *
     * @param binder объект для настройки привязки данных
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("pageNumber", "pageSize", "completed", "name", "description");
    }

    /**
     * Отображает список задач с возможностью фильтрации.
     *
     * @param model модель для передачи данных в представление
     * @param taskFilter фильтр задач
     * @param bindingResult результат валидации фильтра
     * @return имя представления для отображения списка задач
     */
    @GetMapping
    public String listTasks(Model model, @Valid TaskFilter taskFilter, BindingResult bindingResult) {
        model.addAttribute("pageTitle", "Список задач");
        model.addAttribute("page", taskService.filterBy(taskFilter));
        model.addAttribute("taskFilter", taskFilter);
        return "tasks/list";
    }

    /**
     * Отображает информацию о задаче по её идентификатору.
     *
     * @param id идентификатор задачи
     * @param model модель для передачи данных в представление
     * @return имя представления для отображения задачи
     */
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable UUID id, Model model) {
        Optional<TaskModel> task = taskService.getTaskById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        TaskDto taskDto = TaskMapper.INSTANCE.toDto(task.get());
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("pageTitle", "Просмотр задачи с id: " + id);
        return "tasks/view";
    }

    /**
     * Отображает форму для добавления новой задачи.
     *
     * @param model модель для передачи данных в представление
     * @return имя представления для отображения формы добавления
     */
    @GetMapping("/add")
    public String addTaskForm(Model model) {
        model.addAttribute("taskDto", new TaskDto());
        model.addAttribute("pageTitle", "Добавление");
        return "tasks/add";
    }

    /**
     * Обрабатывает отправку формы для добавления новой задачи.
     *
     * @param model модель для передачи данных в представление
     * @param dto объект задачи для добавления
     * @param bindingResult результат валидации данных формы
     * @return перенаправление на список задач при успешном добавлении или отображение формы при ошибках
     */
    @PostMapping("/add")
    public String addTask(Model model, @Valid TaskDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taskDto", dto);
            model.addAttribute("bindingErrors", bindingResult.getAllErrors());
            return "tasks/add";
        }
        taskService.addTask(dto);
        return "redirect:/tasks";
    }

    /**
     * Отображает форму для редактирования существующей задачи.
     *
     * @param id идентификатор задачи
     * @param model модель для передачи данных в представление
     * @return имя представления для отображения формы редактирования
     */
    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable UUID id, Model model) {
        Optional<TaskModel> task = taskService.getTaskById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        TaskDto taskDto = TaskMapper.INSTANCE.toDto(task.get());
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("pageTitle", "Редактирование задачи с id: " + id);
        return "tasks/edit";
    }

    /**
     * Обрабатывает отправку формы для обновления существующей задачи.
     *
     * @param model модель для передачи данных в представление
     * @param id идентификатор задачи
     * @param dto объект задачи с обновленными данными
     * @param bindingResult результат валидации данных формы
     * @return перенаправление на список задач при успешном обновлении или отображение формы при ошибках
     */
    @PutMapping("/edit/{id}")
    public String updateTask(Model model, @PathVariable UUID id, @Valid TaskDto dto, BindingResult bindingResult) {
        Optional<TaskModel> task = taskService.getTaskById(id);
        if (task.isEmpty()) {
            bindingResult.rejectValue("id", "task.notfound", MessageFormat.format("Задача с id: {0} не найдена", id));
            throw new TaskNotFoundException(id);
        }
        if (bindingResult.hasErrors()) {
            dto.setId(id);
            model.addAttribute("taskDto", dto);
            model.addAttribute("pageTitle", "Редактирование задачи с id: " + id);
            model.addAttribute("bindingErrors", bindingResult.getAllErrors());
            return "tasks/edit";
        }
        TaskDto updatedTask = TaskMapper.INSTANCE.toDto(task.get());
        updatedTask.setId(id);
        updatedTask.setName(dto.getName());
        updatedTask.setCompleted(dto.isCompleted());
        updatedTask.setDescription(dto.getDescription());
        taskService.updateTask(updatedTask);
        return "redirect:/tasks";
    }

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return перенаправление на список задач после успешного удаления
     */
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable UUID id) {
        Optional<TaskModel> task = taskService.getTaskById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
