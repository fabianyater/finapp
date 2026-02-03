package com.fyr.finapp.domain.exception;

public class DomainException extends RuntimeException {
    private final DomainErrorCode code;
    private final ErrorCategory category;

    protected DomainException(String message, DomainErrorCode code, ErrorCategory category) {
        super(message);
        this.code = code;
        this.category = category;
    }

    public DomainErrorCode getCode() {
        return code;
    }

    public ErrorCategory getCategory() {
        return category;
    }
}
