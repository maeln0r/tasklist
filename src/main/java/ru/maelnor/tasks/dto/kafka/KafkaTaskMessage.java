package ru.maelnor.tasks.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.maelnor.tasks.dto.TaskDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaTaskMessage {
    private UUID id;
    private String name;
    private String description;
    private boolean completed;
    private TaskStatus status;

    public KafkaTaskMessage(TaskDto taskDto, TaskStatus status) {
        this.id = taskDto.getId();
        this.name = taskDto.getName();
        this.completed = taskDto.isCompleted();
        this.description = taskDto.getDescription();
        this.status = status;
    }
}
