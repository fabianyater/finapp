package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCode;
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
    public record ApiError(String status, int code, String message, Map<String, List<String>> errors) {
    }

    private static HttpStatus statusOf(ErrorCode code) {
        return switch (code.category()) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case CONFLICT -> HttpStatus.CONFLICT;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
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
        var body = new ApiError(
                ex.getCode().category().name(),
                statusOf(ex.getCode()).value(),
                ex.getMessage(),
                Map.of()
        );

        return ResponseEntity.status(statusOf(ex.getCode())).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        var body = new ApiError(
                ex.getClass().getSimpleName(),
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid credentials",
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
