package com.fyr.finapp.domain.model.category.exception;

import com.fyr.finapp.domain.exception.DomainErrorCode;
import com.fyr.finapp.domain.exception.ErrorCategory;

public enum CategoryErrorCode implements DomainErrorCode {
    ID_REQUIRED(ErrorCategory.VALIDATION),
    ID_INVALID(ErrorCategory.VALIDATION),
    NAME_REQUIRED(ErrorCategory.VALIDATION),
    NAME_TOO_LONG(ErrorCategory.VALIDATION),
    NAME_ALREADY_EXISTS(ErrorCategory.VALIDATION);

    private final ErrorCategory category;

    CategoryErrorCode(ErrorCategory category) {
        this.category = category;
    }

    public ErrorCategory category() {
        return category;
    }
}
