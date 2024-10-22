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

@Controller
@RequestMapping("/tasks")
public class TaskWebController {

    private final TaskService taskService;

    public TaskWebController(TaskService taskService) {
        this.taskService = taskService;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("pageNumber", "pageSize", "completed", "name", "description");
    }

    @GetMapping
    public String listTasks(Model model, @Valid TaskFilter taskFilter, BindingResult bindingResult) {
        model.addAttribute("pageTitle", "Список задач");
        model.addAttribute("page", taskService.filterBy(taskFilter));
        model.addAttribute("taskFilter", taskFilter);
        return "tasks/list";
    }

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

    @GetMapping("/add")
    public String addTaskForm(Model model) {
        model.addAttribute("taskDto", new TaskDto());
        model.addAttribute("pageTitle", "Добавление");
        return "tasks/add";
    }

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
