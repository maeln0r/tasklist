package ru.maelnor.tasks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Модель пользователя, представляющая данные о пользователе.
 * Содержит идентификатор пользователя, имя, email
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
