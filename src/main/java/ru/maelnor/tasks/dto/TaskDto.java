package ru.maelnor.tasks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class TaskDto {
    /**
     * Тут специально оставляю UUID, что бы ловить ошибку приведения типов,
     * как поправить смотреть тут: {@link ru.maelnor.tasks.dto.filter.TaskFilterDto}
     */
    private UUID id;
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 5, max = 255, message = "Имя должно быть в пределах от {min} до {max} символов")
    private String name;
    @Size(max = 1000, message = "Описание не должно превышать {max} символов")
    @Pattern(regexp = "^$|.{10,}", message = "Описание должно быть пустым или содержать минимум 10 символов")
    private String description;
    private boolean completed;
}