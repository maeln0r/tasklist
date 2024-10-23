package ru.maelnor.tasks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Модель задачи, представляющая данные о задаче.
 * Содержит идентификатор задачи, её имя, описание и статус завершенности.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private UUID id;
    private String username;
    private String email;
}
