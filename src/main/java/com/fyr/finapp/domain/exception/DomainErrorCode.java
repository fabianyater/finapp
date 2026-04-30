package com.fyr.finapp.domain.exception;

public interface DomainErrorCode {
    ErrorCategory category();
    String name();
}
