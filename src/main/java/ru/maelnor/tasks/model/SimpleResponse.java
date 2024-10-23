package ru.maelnor.tasks.model;

import lombok.*;

/**
 * Простая модель ответа, содержащая только сообщение.
 * Используется для передачи простого текстового ответа в различных сценариях.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleResponse {
    private String message;
}
