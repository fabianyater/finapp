package com.fyr.finapp.domain.exception;

public class ForbiddenException extends DomainException {
    public ForbiddenException(String message, DomainErrorCode code) {
        super(message, code, ErrorCategory.FORBIDDEN);
    }
}
