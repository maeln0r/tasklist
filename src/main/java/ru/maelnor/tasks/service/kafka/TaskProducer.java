package ru.maelnor.tasks.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.kafka.TaskStatus;
import ru.maelnor.tasks.dto.kafka.KafkaTaskMessage;

@Service
public class TaskProducer {
    private static final String TOPIC = "task_list";

    private final KafkaTemplate<String, KafkaTaskMessage> kafkaTemplate;

    public TaskProducer(KafkaTemplate<String, KafkaTaskMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(TaskDto taskDto, TaskStatus taskStatus) {
        KafkaTaskMessage message = new KafkaTaskMessage(taskDto, taskStatus);
        kafkaTemplate.send(TOPIC, message);
    }
}
