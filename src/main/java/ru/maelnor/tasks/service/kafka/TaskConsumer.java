package ru.maelnor.tasks.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.dto.TaskDto;

@Service
@Slf4j
public class TaskConsumer {

    @KafkaListener(topics = "task_list", groupId = "task-list-group")
    public void consume(TaskDto taskDto) {
        log.info("Получено сообщение из Kafka: {}", taskDto);
    }
}
