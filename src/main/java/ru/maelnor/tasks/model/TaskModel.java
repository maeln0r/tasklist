package ru.maelnor.tasks.model;

import lombok.*;

import java.util.UUID;

/**
 * Модель задачи, представляющая данные о задаче.
 * Содержит идентификатор задачи, её имя, описание и статус завершенности.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TaskModel {
    private UUID id;
    private String name;
    private String description;
    private boolean completed;
}
