package ru.maelnor.tasks.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.mapper.TaskFilterMapper;
import ru.maelnor.tasks.mapper.TaskMapper;
import ru.maelnor.tasks.model.TaskFilterModel;
import ru.maelnor.tasks.model.TaskModel;
import ru.maelnor.tasks.service.TaskFilterService;
import ru.maelnor.tasks.service.TaskService;
import ru.maelnor.tasks.service.UserService;
import ru.maelnor.tasks.utils.BindingResultParser;

import java.util.UUID;

/**
 * Веб-контроллер для управления задачами. Обрабатывает запросы для отображения, создания, редактирования и удаления задач.
 * Использует шаблоны представления для отображения страниц.
 */
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskWebController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskFilterService taskFilterService;

    /**
     * Инициализирует разрешенные поля для привязки данных.
     *
     * @param binder объект для настройки привязки данных
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("pageNumber", "pageSize", "completed", "name", "description", "ownerId");
    }

    /**
     * Отображает список задач с возможностью фильтрации.
     *
     * @param model         модель для передачи данных в представление
     * @param taskFilterDto фильтр задач
     * @param bindingResult результат валидации фильтра
     * @return имя представления для отображения списка задач
     */
    @GetMapping
    public String listTasks(Model model, @Valid TaskFilterDto taskFilterDto, BindingResult bindingResult) {
        model.addAttribute("pageTitle", "Список задач");
        model.addAttribute("taskFilter", taskFilterDto);

        boolean filterNotEmpty = taskFilterService.isFilterNotEmpty(TaskFilterMapper.INSTANCE.toModel(taskFilterDto));
        if (taskFilterService.canAccessOwnerId()) {
            model.addAttribute("canAccessOwnerId", true);
            model.addAttribute("userList", userService.getUsesWithTask());
        }

        if (filterNotEmpty && bindingResult.hasErrors()) {
            model.addAttribute("filterError", BindingResultParser.parseErrors(bindingResult));
            return "tasks/list";
        }

        TaskFilterModel filterModel = TaskFilterMapper.INSTANCE.toModel(taskFilterDto);
        Page<TaskDto> result = taskService.filterBy(filterModel).map(TaskMapper.INSTANCE::toDto);

        model.addAttribute("page", result);
        return "tasks/list";
    }

    /**
     * Отображает информацию о задаче по её идентификатору.
     *
     * @param id    идентификатор задачи
     * @param model модель для передачи данных в представление
     * @return имя представления для отображения задачи
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER') or @taskPermissionEvaluator.isTaskOwner(#id)")
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable UUID id, Model model) {
        TaskModel task = taskService.getTaskById(id).orElseThrow(() -> new TaskNotFoundException(id));
        model.addAttribute("taskDto", TaskMapper.INSTANCE.toDto(task));
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
     * @param model         модель для передачи данных в представление
     * @param dto           объект задачи для добавления
     * @param bindingResult результат валидации данных формы
     * @return перенаправление на список задач при успешном добавлении или отображение формы при ошибках
     */
    @PostMapping("/add")
    public String addTask(Model model, @Valid TaskDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taskDto", dto);
            model.addAttribute("bindingErrors", BindingResultParser.parseErrors(bindingResult));
            return "tasks/add";
        }
        taskService.addTask(TaskMapper.INSTANCE.toModel(dto));
        return "redirect:/tasks";
    }

    /**
     * Отображает форму для редактирования существующей задачи.
     *
     * @param id    идентификатор задачи
     * @param model модель для передачи данных в представление
     * @return имя представления для отображения формы редактирования
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or @taskPermissionEvaluator.isTaskOwner(#id)")
    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable UUID id, Model model) {
        TaskModel task = taskService.getTaskById(id).orElseThrow(() -> new TaskNotFoundException(id));
        model.addAttribute("taskDto", TaskMapper.INSTANCE.toDto(task));
        model.addAttribute("pageTitle", "Редактирование задачи с id: " + id);
        return "tasks/edit";
    }

    /**
     * Обрабатывает отправку формы для обновления существующей задачи.
     *
     * @param model         модель для передачи данных в представление
     * @param id            идентификатор задачи
     * @param dto           объект задачи с обновленными данными
     * @param bindingResult результат валидации данных формы
     * @return перенаправление на список задач при успешном обновлении или отображение формы при ошибках
     */
    @PutMapping("/edit/{id}")
    public String updateTask(Model model, @PathVariable UUID id, @Valid TaskDto dto, BindingResult bindingResult) {
        TaskModel task = taskService.getTaskById(id).orElseThrow(() -> new TaskNotFoundException(id));
        if (bindingResult.hasErrors()) {
            dto.setId(id);
            model.addAttribute("taskDto", dto);
            model.addAttribute("pageTitle", "Редактирование задачи с id: " + id);
            model.addAttribute("bindingErrors", BindingResultParser.parseErrors(bindingResult));
            return "tasks/edit";
        }

        BeanUtils.copyProperties(dto, task, "id");

        taskService.updateTask(task);
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
        if (taskService.getTaskById(id).isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
