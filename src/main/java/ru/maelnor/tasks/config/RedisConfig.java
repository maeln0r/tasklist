package ru.maelnor.tasks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import ru.maelnor.tasks.entity.RefreshTokenEntity;

import java.time.Duration;
import java.util.Collections;

/**
 * Конфигурационный класс для настройки Redis в приложении.
 * Настраивает соединение с Redis и конфигурацию для хранения сущностей, таких как RefreshToken.
 */
@Configuration
@EnableRedisRepositories(
        keyspaceConfiguration = RedisConfig.RefreshTokenKeyspaceConfiguration.class,
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
)
public class RedisConfig {

    /**
     * Параметр, указывающий срок действия refresh токена.
     * Загружается из файла настроек приложения.
     */
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    /**
     * Создает бин {@link JedisConnectionFactory} для подключения к Redis с использованием
     * настроек, загруженных из {@link RedisProperties}.
     *
     * @param redisProperties настройки подключения к Redis: такие, как хост и порт
     * @return настроенная фабрика соединений Redis
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        return new JedisConnectionFactory(configuration);
    }

    /**
     * Класс для настройки keyspace (пространства ключей) для сущности {@link RefreshTokenEntity}.
     * Определяет keyspace для хранения refresh токенов в Redis и устанавливает срок действия ключей.
     */
    public class RefreshTokenKeyspaceConfiguration extends KeyspaceConfiguration {

        private static final String REFRESH_TOKEN_KEYSPACE = "refresh_token";

        /**
         * Задает начальные настройки для keyspace, включая срок действия токенов.
         *
         * @return настройки keyspace для сущности RefreshTokenEntity
         */
        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(RefreshTokenEntity.class, REFRESH_TOKEN_KEYSPACE);
            keyspaceSettings.setTimeToLive(refreshTokenExpiration.getSeconds()); // Устанавливает время жизни ключей
            return Collections.singleton(keyspaceSettings);
        }
    }
}
