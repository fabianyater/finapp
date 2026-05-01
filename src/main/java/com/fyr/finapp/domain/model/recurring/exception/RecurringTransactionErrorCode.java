package com.fyr.finapp.domain.model.recurring.exception;

import com.fyr.finapp.domain.exception.DomainErrorCode;
import com.fyr.finapp.domain.exception.ErrorCategory;

public enum RecurringTransactionErrorCode implements DomainErrorCode {
    ID_REQUIRED(ErrorCategory.VALIDATION),
    ID_INVALID(ErrorCategory.VALIDATION),
    AMOUNT_MUST_BE_POSITIVE(ErrorCategory.VALIDATION),
    DESCRIPTION_REQUIRED(ErrorCategory.VALIDATION),
    NOT_FOUND(ErrorCategory.NOT_FOUND),
    ACCESS_DENIED(ErrorCategory.FORBIDDEN),
    ACCOUNT_ARCHIVED(ErrorCategory.VALIDATION);

    private final ErrorCategory category;

    RecurringTransactionErrorCode(ErrorCategory category) {
        this.category = category;
    }

    @Override
    public ErrorCategory category() {
        return category;
    }
}
