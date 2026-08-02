package io.github.opencivilizationplatform.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp,
    String requestId
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message, String requestId) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), requestId);
    }
}
