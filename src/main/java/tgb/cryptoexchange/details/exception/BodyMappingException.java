package tgb.cryptoexchange.details.exception;

/**
 * Пробрасывается при ошибках парсинга тела в JSON.
 */
public class BodyMappingException extends RuntimeException {
    public BodyMappingException() {
    }

    public BodyMappingException(String message) {
        super(message);
    }

    public BodyMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
