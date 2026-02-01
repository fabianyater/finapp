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

    public enum ErrorCode {
        USER_ID_REQUIRED,
        PREFERENCE_ID_REQUIRED,

        NAME_REQUIRED,
        NAME_BLANK,
        NAME_TOO_LONG,

        USERNAME_REQUIRED,
        USERNAME_INVALID,

        EMAIL_REQUIRED,
        EMAIL_BLANK,
        EMAIL_TOO_LONG,
        EMAIL_INVALID,
        EMAIL_ALREADY_EXISTS,

        PASSWORD_REQUIRED,
        PASSWORD_BLANK,
        PASSWORD_TOO_SHORT,
        PASSWORD_TOO_LONG,

        PASSWORD_HASH_REQUIRED,
        PASSWORD_HASH_BLANK,
        PASSWORD_HASH_TOO_SHORT,

        LOCALE_REQUIRED,
        LOCALE_BLANK,
        LOCALE_INVALID,

        CURRENCY_REQUIRED,
        CURRENCY_INVALID,

        TIMEZONE_REQUIRED,
        TIMEZONE_BLANK,
        TIMEZONE_INVALID,

        FIRST_DAY_OF_WEEK_INVALID,

        DATE_FORMAT_REQUIRED,
        DATE_FORMAT_BLANK,
        DATE_FORMAT_TOO_LONG
    }
}
