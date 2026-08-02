package io.github.opencivilizationplatform.saga;

public class SagaException extends RuntimeException {
    public SagaException(String message, Throwable cause) {
        super(message, cause);
    }
}
