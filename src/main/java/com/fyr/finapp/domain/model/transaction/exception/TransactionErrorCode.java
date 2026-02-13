package com.fyr.finapp.domain.model.transaction.exception;

import com.fyr.finapp.domain.exception.DomainErrorCode;
import com.fyr.finapp.domain.exception.ErrorCategory;

public enum TransactionErrorCode implements DomainErrorCode {
    ID_REQUIRED(ErrorCategory.VALIDATION),
    ID_INVALID(ErrorCategory.VALIDATION),
    AMOUNT_NEGATIVE(ErrorCategory.VALIDATION),
    DESCRIPTION_TOO_LONG(ErrorCategory.VALIDATION),
    ACCOUNT_NOT_FOUND(ErrorCategory.NOT_FOUND),
    ACCESS_DENIED(ErrorCategory.FORBIDDEN),
    TRANSACTION_NOT_FOUND(ErrorCategory.NOT_FOUND),
    TRANSACTION_ALREADY_ARCHIVED(ErrorCategory.VALIDATION),
    DESCRIPTION_REQUIRED(ErrorCategory.VALIDATION);

    private final ErrorCategory category;

    TransactionErrorCode(ErrorCategory category) {
        this.category = category;
    }

    @Override
    public ErrorCategory category() {
        return null;
    }
}
