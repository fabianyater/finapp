package com.fyr.finapp.domain.model.user.exception;

import com.fyr.finapp.domain.exception.DomainErrorCode;
import com.fyr.finapp.domain.exception.ErrorCategory;

public enum UserErrorCode implements DomainErrorCode {
    ID_REQUIRED(ErrorCategory.VALIDATION),
    ID_INVALID(ErrorCategory.VALIDATION),
    USERNAME_REQUIRED(ErrorCategory.VALIDATION),
    USERNAME_INVALID(ErrorCategory.VALIDATION),
    EMAIL_REQUIRED(ErrorCategory.VALIDATION),
    EMAIL_INVALID(ErrorCategory.VALIDATION),
    EMAIL_ALREADY_EXISTS(ErrorCategory.CONFLICT),
    PASSWORD_TOO_SHORT(ErrorCategory.VALIDATION),
    DATE_FORMAT_REQUIRED(ErrorCategory.VALIDATION),
    DATE_FORMAT_BLANK(ErrorCategory.VALIDATION),
    DATE_FORMAT_TOO_LONG(ErrorCategory.VALIDATION),
    LOCALE_REQUIRED(ErrorCategory.VALIDATION),
    LOCALE_BLANK(ErrorCategory.VALIDATION),
    LOCALE_INVALID(ErrorCategory.VALIDATION),
    EMAIL_BLANK(ErrorCategory.VALIDATION),
    EMAIL_TOO_LONG(ErrorCategory.VALIDATION),
    FIRST_DAY_OF_WEEK_INVALID(ErrorCategory.VALIDATION),
    PASSWORD_HASH_REQUIRED(ErrorCategory.VALIDATION),
    PASSWORD_HASH_BLANK(ErrorCategory.VALIDATION),
    PASSWORD_HASH_TOO_SHORT(ErrorCategory.VALIDATION),
    NAME_REQUIRED(ErrorCategory.VALIDATION),
    NAME_BLANK(ErrorCategory.VALIDATION),
    NAME_TOO_LONG(ErrorCategory.VALIDATION),
    PASSWORD_REQUIRED(ErrorCategory.VALIDATION),
    PASSWORD_BLANK(ErrorCategory.VALIDATION),
    PASSWORD_TOO_WEAK(ErrorCategory.VALIDATION),
    PASSWORD_TOO_LONG(ErrorCategory.VALIDATION),
    PREFERENCE_ID_REQUIRED(ErrorCategory.VALIDATION),
    TIMEZONE_REQUIRED(ErrorCategory.VALIDATION),
    TIMEZONE_BLANK(ErrorCategory.VALIDATION),
    TIMEZONE_INVALID(ErrorCategory.VALIDATION);

    private final ErrorCategory category;

    UserErrorCode(ErrorCategory category) {
        this.category = category;
    }

    public ErrorCategory category() {
        return category;
    }
}
