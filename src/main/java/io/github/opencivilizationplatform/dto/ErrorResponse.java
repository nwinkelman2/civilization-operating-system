package io.github.opencivilizationplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Standard error response")
public record ErrorResponse(
    @Schema(description = "Error type") String error,
    @Schema(description = "Error description") String message,
    @Schema(description = "Error timestamp") LocalDateTime timestamp,
    @Schema(description = "Field-level validation errors") List<FieldError> fieldErrors
) {
    public ErrorResponse(String error, String message) {
        this(error, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(String error, String message, List<FieldError> fieldErrors) {
        this(error, message, LocalDateTime.now(), fieldErrors);
    }

    @Schema(description = "Field validation error")
    public record FieldError(
        @Schema(description = "Field name") String field,
        @Schema(description = "Validation error message") String message
    ) {}
}
