package com.fyr.finapp.domain.common.exception;

import com.fyr.finapp.domain.exception.DomainErrorCode;
import com.fyr.finapp.domain.exception.ErrorCategory;

public enum SharedErrorCode implements DomainErrorCode {
    INVALID_COLOR_FORMAT(ErrorCategory.VALIDATION),
    INVALID_CURRENCY_CODE(ErrorCategory.VALIDATION),
    CURRENCY_REQUIRED(ErrorCategory.VALIDATION),
    ICON_NAME_REQUIRED(ErrorCategory.VALIDATION),
    ICON_NAME_TOO_LONG(ErrorCategory.VALIDATION),
    AMOUNT_REQUIRED(ErrorCategory.VALIDATION),
    CURRENCY_MISMATCH(ErrorCategory.VALIDATION);


    private final ErrorCategory category;

    SharedErrorCode(ErrorCategory category) {
        this.category = category;
    }

    public ErrorCategory category() {
        return category;
    }
}
