package ru.maelnor.tasks.model;

import lombok.*;

import java.util.UUID;

/**
 * Модель фильтра задачи, представляющая данные о задаче.
 * Содержит параметры, которые могут быть использованы для фильтрации списка задач.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TaskFilterModel {
    private Integer pageNumber;
    private Integer pageSize;
    private String name;
    private Boolean completed;
    private UUID ownerId;
}
