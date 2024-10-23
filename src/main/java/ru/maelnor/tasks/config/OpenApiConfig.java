package ru.maelnor.tasks.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурационный класс для настройки OpenAPI (Swagger) для среды разработки.
 * Генерирует документацию для API управления задачами.
 */
@Configuration
@Profile("dev")
public class OpenApiConfig {

    /**
     * Создает и настраивает объект {@link OpenAPI}, который описывает API приложения.
     * Включает информацию о версии API, контактные данные, лицензию и ссылки на внешнюю документацию.
     *
     * @return настроенный объект {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management API")  // Название API
                        .version("1.0")  // Версия API
                        .description("API для управления задачами")  // Описание API
                        .contact(new Contact()
                                .name("Maelnor")  // Имя контакта
                                .url("https://maelnor.ru")  // URL контакта
                                .email("support@maelnor.ru"))  // Email для связи
                        .license(new License()
                                .name("Apache 2.0")  // Лицензия
                                .url("http://springdoc.org")))  // URL лицензии
                .externalDocs(new ExternalDocumentation()
                        .description("Документация SpringDoc OpenAPI")  // Описание внешней документации
                        .url("https://springdoc.org"));  // URL внешней документации
    }
}
