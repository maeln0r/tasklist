package ru.maelnor.tasks.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Класс конфигурации для настройки кэширования с использованием Caffeine.
 * Этот класс определяет бины для менеджера кэша и построителя кэша.
 */
@Configuration
@EnableCaching
public class CaffeineConfig {
    /**
     * Создает и настраивает бин {@link CacheManager} с использованием Caffeine.
     * Менеджер кэша управляет кэшем с именем "tasks" и настраивается с помощью
     * параметров, заданных в методе {@link #caffeineCacheBuilder()}.
     *
     * @return настроенный {@link CacheManager}
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("tasks");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Настраивает построитель кэша Caffeine с начальными параметрами:
     * - начальная емкость,
     * - максимальный размер,
     * - политика истечения по времени,
     * - запись статистики.
     *
     * @return построитель кэша {@link Caffeine}
     */
    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats();
    }
}
