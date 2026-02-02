package com.fyr.finapp.domain.exception;

public class DomainException extends RuntimeException{
    private final ErrorCode code;

    public DomainException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
