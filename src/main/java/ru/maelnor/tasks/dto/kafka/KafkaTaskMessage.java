package ru.maelnor.tasks.dto.kafka;

import ru.maelnor.tasks.dto.TaskDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaTaskMessage {
    private Long id;
    private String name;
    private boolean completed;
    private TaskStatus status;

    public KafkaTaskMessage(TaskDto taskDto, TaskStatus status) {
        this.id = taskDto.getId();
        this.name = taskDto.getName();
        this.completed = taskDto.isCompleted();
        this.status = status;
    }
}
