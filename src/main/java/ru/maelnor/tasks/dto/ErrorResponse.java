package ru.maelnor.tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO для передачи информации об ошибках.
 * Содержит сообщение об ошибке, статус HTTP-ответа и уникальный идентификатор запроса.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private UUID uuid;
}
