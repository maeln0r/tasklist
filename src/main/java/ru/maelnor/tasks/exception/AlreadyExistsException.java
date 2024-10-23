package ru.maelnor.tasks.exception;

/**
 * Исключение, выбрасываемое при попытке создания объекта, который уже существует.
 * Наследуется от {@link RuntimeException}.
 */
public class AlreadyExistsException extends RuntimeException {
    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}
