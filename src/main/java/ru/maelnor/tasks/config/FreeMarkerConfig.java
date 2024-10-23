package ru.maelnor.tasks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

/**
 * Класс конфигурации для настройки FreeMarker в приложении.
 * Настраивает загрузчик шаблонов и свойства для FreeMarker.
 */
@Configuration
public class FreeMarkerConfig {

    /**
     * Создает и настраивает бин {@link FreeMarkerConfigurer}.
     * Определяет путь к шаблонам и задает настройки для FreeMarker, такие как
     * формат отображения логических значений.
     *
     * @return настроенный {@link FreeMarkerConfigurer}
     */
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates"); // Указывает путь к шаблонам

        // Устанавливаем свойства для FreeMarker
        Properties settings = new Properties();
        settings.setProperty("boolean_format", "yes,no"); // Задаем формат отображения булевых значений
        configurer.setFreemarkerSettings(settings);

        return configurer;
    }
}
