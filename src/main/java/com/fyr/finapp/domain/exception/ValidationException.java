package com.fyr.finapp.domain.exception;

public class ValidationException extends DomainException {
    public ValidationException(String message, DomainErrorCode code) {
        super(message, code, ErrorCategory.VALIDATION);
    }
}
