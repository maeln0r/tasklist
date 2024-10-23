package ru.maelnor.tasks.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.maelnor.tasks.dto.kafka.KafkaTaskMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс конфигурации для настройки Kafka в приложении.
 * Настраивает продюсеров, консумеров и фабрику слушателей Kafka.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    /**
     * URL-адреса серверов Kafka, загружаемые из файла настроек приложения.
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Идентификатор группы консумеров Kafka, загружаемый из файла настроек приложения.
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Создает бин фабрики продюсеров Kafka для отправки сообщений с ключами типа {@link String}
     * и значениями типа {@link KafkaTaskMessage}.
     *
     * @return фабрика продюсеров Kafka
     */
    @Bean
    public ProducerFactory<String, KafkaTaskMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Создает бин {@link KafkaTemplate}, используемый для отправки сообщений в Kafka.
     *
     * @return настроенный шаблон Kafka
     */
    @Bean
    public KafkaTemplate<String, KafkaTaskMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Создает бин фабрики консумеров Kafka для обработки входящих сообщений с ключами типа {@link String}
     * и значениями типа {@link KafkaTaskMessage}.
     *
     * @return фабрика консумеров Kafka
     */
    @Bean
    public ConsumerFactory<String, KafkaTaskMessage> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Разрешаем все пакеты для десериализации
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Создает бин фабрики слушателей Kafka для обработки сообщений.
     * Фабрика использует созданный ранее бин {@link ConsumerFactory}.
     *
     * @return фабрика слушателей Kafka
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
