package ru.maelnor.tasks.exception;

import java.text.MessageFormat;

/**
 * Исключение, выбрасываемое при возникновении ошибок, связанных с обновлением refresh токена.
 * Наследуется от {@link RuntimeException}.
 */
public class RefreshTokenException extends RuntimeException {

    /**
     * Создает исключение с указанным токеном и сообщением об ошибке.
     *
     * @param token   токен, который вызвал ошибку
     * @param message сообщение, описывающее причину исключения
     */
    public RefreshTokenException(String token, String message) {
        super(MessageFormat.format("Ошибка обновления токена: {0} : {1}", token, message));
    }

    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public RefreshTokenException(String message) {
        super(message);
    }
}
