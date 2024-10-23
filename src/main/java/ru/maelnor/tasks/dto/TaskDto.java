package ru.maelnor.tasks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO для передачи информации о задачах.
 * Используется для создания, обновления и передачи данных о задачах в системе.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDto {
    private UUID id;
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 5, max = 255, message = "Имя должно быть в пределах от {min} до {max} символов")
    private String name;
    private String description;
    private boolean completed;
}