package ru.maelnor.tasks.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.maelnor.tasks.dto.TaskDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO для сообщений, отправляемых через Kafka.
 * Используется для передачи информации о задачах с помощью Kafka.
 */
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

    /**
     * Создает объект KafkaTaskMessage на основе {@link TaskDto} и статуса задачи.
     *
     * @param taskDto объект задачи
     * @param status статус задачи
     */
    public KafkaTaskMessage(TaskDto taskDto, TaskStatus status) {
        this.id = taskDto.getId();
        this.name = taskDto.getName();
        this.completed = taskDto.isCompleted();
        this.description = taskDto.getDescription();
        this.status = status;
    }
}
