package ru.maelnor.tasks.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.exception.TaskNotFoundException;
import ru.maelnor.tasks.repository.JpaTaskRepository;
import ru.maelnor.tasks.repository.TaskRepository;
import ru.maelnor.tasks.service.CurrentUserService;

import java.util.UUID;

/**
 * Компонент для проверки прав доступа к задачам.
 *
 * @see TaskRepository
 * @see CurrentUserService
 * @see ru.maelnor.tasks.entity.TaskEntity
 * @see ru.maelnor.tasks.exception.TaskNotFoundException
 */
@Component
@RequiredArgsConstructor
public class TaskPermissionEvaluator {

    private final JpaTaskRepository taskRepository;
    private final CurrentUserService currentUserService;

    /**
     * Проверяет, является ли текущий пользователь владельцем задачи.
     *
     * @param taskId идентификатор задачи для проверки
     * @return {@code true}, если текущий пользователь является владельцем задачи;
     * {@code false} в противном случае
     * @throws TaskNotFoundException если задача с заданным идентификатором не найдена
     */
    public boolean isTaskOwner(UUID taskId) {
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return taskEntity.getOwner().getId().equals(currentUserService.getCurrentUser().getId());
    }
}
