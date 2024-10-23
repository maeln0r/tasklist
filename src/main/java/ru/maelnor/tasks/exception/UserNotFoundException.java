package ru.maelnor.tasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Исключение, выбрасываемое, когда пользователь с указанным идентификатором не найден.
 * Маркировано аннотацией {@link ResponseStatus} для возвращения HTTP статуса 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Создает исключение с сообщением, указывающим, что пользователь с данным идентификатором не найден.
     *
     * @param id идентификатор пользователя, который не был найден
     */
    public UserNotFoundException(UUID id) {
        super("Пользователь с id: " + id + " не найден");
    }
}
