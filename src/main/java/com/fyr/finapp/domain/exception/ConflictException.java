package com.fyr.finapp.domain.exception;

public class ConflictException extends DomainException {
    public ConflictException(String message, DomainErrorCode code) {
        super(message, code, ErrorCategory.CONFLICT);
    }
}
