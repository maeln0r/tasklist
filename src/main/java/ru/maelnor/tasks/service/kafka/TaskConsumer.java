package ru.maelnor.tasks.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.dto.kafka.KafkaTaskMessage;

@Service
@Slf4j
public class TaskConsumer {

    @KafkaListener(topics = "task_list", groupId = "task-list-group")
    public void consume(KafkaTaskMessage message) {
        log.error("Получено сообщение из Kafka: {}", message);
    }
}
