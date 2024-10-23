package ru.maelnor.tasks.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.dto.kafka.TaskStatus;
import ru.maelnor.tasks.dto.kafka.KafkaTaskMessage;

/**
 * Сервис для отправки сообщений в Kafka.
 * Отправляет сообщения о задачах в топик "task_list".
 */
@Service
public class TaskProducer {

    private static final String TOPIC = "task_list";
    private final KafkaTemplate<String, KafkaTaskMessage> kafkaTemplate;

    /**
     * Конструктор, принимающий {@link KafkaTemplate} для отправки сообщений в Kafka.
     *
     * @param kafkaTemplate шаблон для работы с Kafka
     */
    public TaskProducer(KafkaTemplate<String, KafkaTaskMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение в топик "task_list" с информацией о задаче и её статусе.
     *
     * @param taskDto    объект задачи {@link TaskDto}, содержащий данные о задаче
     * @param taskStatus статус задачи {@link TaskStatus}
     */
    public void sendMessage(TaskDto taskDto, TaskStatus taskStatus) {
        KafkaTaskMessage message = new KafkaTaskMessage(taskDto, taskStatus);
        kafkaTemplate.send(TOPIC, message);
    }
}
