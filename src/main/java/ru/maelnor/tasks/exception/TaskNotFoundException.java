package ru.maelnor.tasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Исключение, выбрасываемое, когда задача с указанным идентификатором не найдена.
 * Маркировано аннотацией {@link ResponseStatus} для возвращения HTTP статуса 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeException {

    /**
     * Создает исключение с сообщением, указывающим, что задача с данным идентификатором не найдена.
     *
     * @param id идентификатор задачи, которая не была найдена
     */
    public TaskNotFoundException(UUID id) {
        super("Задача с id: " + id + " не найдена");
    }
}
