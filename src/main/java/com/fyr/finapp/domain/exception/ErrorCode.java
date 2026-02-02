package com.fyr.finapp.domain.exception;

public enum ErrorCode {
    USER_ID_REQUIRED(ErrorCategory.VALIDATION),
    PREFERENCE_ID_REQUIRED(ErrorCategory.VALIDATION),

    NAME_REQUIRED(ErrorCategory.VALIDATION),
    NAME_BLANK(ErrorCategory.VALIDATION),
    NAME_TOO_LONG(ErrorCategory.VALIDATION),

    USERNAME_REQUIRED(ErrorCategory.VALIDATION),
    USERNAME_INVALID(ErrorCategory.VALIDATION),

    EMAIL_REQUIRED(ErrorCategory.VALIDATION),
    EMAIL_BLANK(ErrorCategory.VALIDATION),
    EMAIL_TOO_LONG(ErrorCategory.VALIDATION),
    EMAIL_INVALID(ErrorCategory.VALIDATION),
    EMAIL_ALREADY_EXISTS(ErrorCategory.CONFLICT),

    PASSWORD_REQUIRED(ErrorCategory.VALIDATION),
    PASSWORD_BLANK(ErrorCategory.VALIDATION),
    PASSWORD_TOO_SHORT(ErrorCategory.VALIDATION),
    PASSWORD_TOO_LONG(ErrorCategory.VALIDATION),

    PASSWORD_HASH_REQUIRED(ErrorCategory.VALIDATION),
    PASSWORD_HASH_BLANK(ErrorCategory.VALIDATION),
    PASSWORD_HASH_TOO_SHORT(ErrorCategory.VALIDATION),

    LOCALE_REQUIRED(ErrorCategory.VALIDATION),
    LOCALE_BLANK(ErrorCategory.VALIDATION),
    LOCALE_INVALID(ErrorCategory.VALIDATION),

    CURRENCY_REQUIRED(ErrorCategory.VALIDATION),
    CURRENCY_INVALID(ErrorCategory.VALIDATION),

    TIMEZONE_REQUIRED(ErrorCategory.VALIDATION),
    TIMEZONE_BLANK(ErrorCategory.VALIDATION),
    TIMEZONE_INVALID(ErrorCategory.VALIDATION),

    FIRST_DAY_OF_WEEK_INVALID(ErrorCategory.VALIDATION),

    DATE_FORMAT_REQUIRED(ErrorCategory.VALIDATION),
    DATE_FORMAT_BLANK(ErrorCategory.VALIDATION),
    DATE_FORMAT_TOO_LONG(ErrorCategory.VALIDATION);

    private final ErrorCategory category;
    ErrorCode(ErrorCategory category) { this.category = category; }
    public ErrorCategory category() { return category; }
}
