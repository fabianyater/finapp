package com.fyr.finapp.domain.model.account.exception;

import com.fyr.finapp.domain.exception.DomainErrorCode;
import com.fyr.finapp.domain.exception.ErrorCategory;

public enum AccountErrorCode implements DomainErrorCode {
    ID_REQUIRED(ErrorCategory.VALIDATION),
    ID_INVALID(ErrorCategory.VALIDATION),
    NAME_REQUIRED(ErrorCategory.VALIDATION),
    NAME_TOO_LONG(ErrorCategory.VALIDATION),
    TYPE_INVALID(ErrorCategory.VALIDATION),
    AMOUNT_NEGATIVE(ErrorCategory.VALIDATION),
    NAME_ALREADY_EXISTS(ErrorCategory.VALIDATION),
    INVALID_INITIAL_BALANCE(ErrorCategory.VALIDATION);

    private final ErrorCategory category;

    AccountErrorCode(ErrorCategory category) {
        this.category = category;
    }

    public ErrorCategory category() {
        return category;
    }
}
