package ru.maelnor.tasks.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.dto.kafka.KafkaTaskMessage;

/**
 * Сервис для потребления сообщений из Kafka.
 * Получает сообщения о задачах из топика "task_list" и обрабатывает их.
 */
@Service
@Slf4j
public class TaskConsumer {

    /**
     * Потребляет сообщения из топика "task_list" в группе "task-list-group".
     * Логирует полученные сообщения типа {@link KafkaTaskMessage}.
     *
     * @param message сообщение, полученное из Kafka
     */
    @KafkaListener(topics = "task_list", groupId = "task-list-group")
    public void consume(KafkaTaskMessage message) {
        log.error("Получено сообщение из Kafka: {}", message);
    }
}
