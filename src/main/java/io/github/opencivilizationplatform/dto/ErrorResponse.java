package io.github.opencivilizationplatform.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String error,
    String message,
    LocalDateTime timestamp,
    List<FieldError> fieldErrors
) {
    public ErrorResponse(String error, String message) {
        this(error, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(String error, String message, List<FieldError> fieldErrors) {
        this(error, message, LocalDateTime.now(), fieldErrors);
    }

    public record FieldError(String field, String message) {}
}
