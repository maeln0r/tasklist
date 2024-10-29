package ru.maelnor.tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.model.TaskFilterModel;

import java.util.Arrays;

/**
 * Сервис для работы с фильтром задач.
 * Позволяет проверить фильтр на наличие значений и проверяет права на доступ к фильтру по id пользователя.
 */
@Service
@RequiredArgsConstructor
public class TaskFilterService {
    private final CurrentUserService currentUserService;

    /**
     * @param taskFilterModel модель фильтра задач
     * @return boolean - признак того, что фильтр не пустой
     */
    public boolean isFilterNotEmpty(TaskFilterModel taskFilterModel) {
        return Arrays.stream(TaskFilterModel.class.getDeclaredFields())
                .anyMatch(field -> {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(taskFilterModel);
                        return value != null && (!(value instanceof String) || !((String) value).isEmpty());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Ошибка доступа к полю: " + field.getName(), e);
                    }
                });
    }

    /**
     * @return boolean - результат проверки на доступность фильтра по id пользователя для текущего пользователя
     */
    public boolean canAccessOwnerId() {
        return currentUserService.getCurrentUser().isAdmin() || currentUserService.getCurrentUser().isManager();
    }
}
