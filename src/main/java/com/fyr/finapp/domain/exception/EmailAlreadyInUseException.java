package com.fyr.finapp.domain.exception;

public class EmailAlreadyInUseException extends DomainException {
    public EmailAlreadyInUseException(String message, ErrorCode code) {
       super(message, code);
    }
}
