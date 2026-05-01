package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCategory;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public record ApiError(String status, int code, String message, Map<String, List<String>> errors) {
    }

    private static HttpStatus statusOf(ErrorCategory category) {
        return switch (category) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case CONFLICT -> HttpStatus.CONFLICT;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
        };
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(OptimisticLockException ex) {
        log.warn("Optimistic lock conflict: {}", ex.getMessage());

        var body = new ApiError(
                "CONCURRENT_MODIFICATION",
                HttpStatus.CONFLICT.value(),
                "This data was modified by another operation. Please refresh and try again.",
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            String field = err.getField();
            String msg = (err.getDefaultMessage() != null) ? err.getDefaultMessage() : "Invalid value";
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(msg);
        }

        var body = new ApiError(
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                "Request validation failed",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainValidation(DomainException ex) {
        String code = ex.getCode() != null ? ex.getCode().name() : ex.getCategory().name();
        HttpStatus status = statusOf(ex.getCategory());

        log.warn("Domain exception [{}] - {}", code, ex.getMessage());

        var body = new ApiError(code, status.value(), ex.getMessage(), Map.of());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        // Spring's internal "Bad credentials" message is not user-friendly
        String message = "Bad credentials".equalsIgnoreCase(ex.getMessage())
                ? "Invalid email or password"
                : ex.getMessage();

        var body = new ApiError("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED.value(), message, Map.of());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        var body = new ApiError(
                "SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error",
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
