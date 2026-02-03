package com.fyr.finapp.domain.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String message, DomainErrorCode code) {
        super(message, code, ErrorCategory.NOT_FOUND);
    }
}
