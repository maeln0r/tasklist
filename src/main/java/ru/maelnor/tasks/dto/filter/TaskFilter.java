package ru.maelnor.tasks.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.maelnor.tasks.validation.TaskFilterValid;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) для фильтрации задач (tasks).
 * Содержит параметры, которые могут быть использованы для фильтрации списка задач.
 */
@Data
@NoArgsConstructor
@TaskFilterValid
@EqualsAndHashCode
public class TaskFilter {
    private Integer pageNumber;
    private Integer pageSize;
    private String name;
    private Boolean completed;
    private UUID ownerId;
}
