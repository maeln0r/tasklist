package ru.maelnor.tasks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskModel {
    private UUID id;
    private String name;
    private String description;
    private boolean completed;
}
