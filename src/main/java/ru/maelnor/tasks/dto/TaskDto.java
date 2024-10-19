package ru.maelnor.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskDto {
    private UUID id;
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 5, max = 255, message = "Имя должно быть в пределах от {min} до {max} символов")
    private String name;
    private boolean completed;
}