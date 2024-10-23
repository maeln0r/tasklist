package ru.maelnor.tasks.dto.kafka;

/**
 * Перечисление статусов задачи для передачи через Kafka.
 */
public enum TaskStatus {
    NEW,
    UPDATED,
    DELETED
}
