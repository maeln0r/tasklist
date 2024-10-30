package ru.maelnor.tasks.mapper;

import org.mapstruct.Named;

import java.util.UUID;

/**
 * Приведение UUID для фильтра задач
 */
public interface MapperTypeCaster {
    /**
     * Преобразует строку в UUID.
     *
     * @param uuid строка, содержащая UUID
     * @return UUID или null, если строка пуста
     */
    @Named("stringToUUID")
    static UUID stringToUUID(String uuid) {
        try {
            return uuid != null && !uuid.isEmpty() ? UUID.fromString(uuid) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Преобразует UUID в строку.
     *
     * @param uuid объект UUID
     * @return строка представления UUID или null, если UUID равен null
     */
    @Named("uuidToString")
    static String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
