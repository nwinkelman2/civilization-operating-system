package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
@Tag(name = "Global Exception Handler", description = "Global exception handling endpoints")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    @Operation(summary = "Handle not found", description = "Handles NoSuchElementException with 404 response")
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Operation(summary = "Handle bad request", description = "Handles IllegalArgumentException with 400 response")
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        log.warn("Bad request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Bad request", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Operation(summary = "Handle validation errors", description = "Handles MethodArgumentNotValidException with 400 response")
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation failed", "Invalid request content", fieldErrors));
    }

    @ExceptionHandler(Exception.class)
    @Operation(summary = "Handle general errors", description = "Handles uncaught exceptions with 500 response")
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error", "An unexpected error occurred"));
    }
}
