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
    INVALID_INITIAL_BALANCE(ErrorCategory.VALIDATION),
    ACCOUNT_NOT_FOUND(ErrorCategory.NOT_FOUND),
    ACCESS_DENIED(ErrorCategory.FORBIDDEN),
    ACCOUNT_ALREADY_ARCHIVED(ErrorCategory.VALIDATION),
    ACCOUNT_NOT_ARCHIVED(ErrorCategory.VALIDATION),
    ACCOUNT_ARCHIVED(ErrorCategory.VALIDATION),
    CURRENCY_MISMATCH(ErrorCategory.VALIDATION),
    INSUFFICIENT_FUNDS(ErrorCategory.VALIDATION),
    MEMBER_NOT_FOUND(ErrorCategory.NOT_FOUND),
    ALREADY_MEMBER(ErrorCategory.VALIDATION),
    CANNOT_INVITE_SELF(ErrorCategory.VALIDATION),
    CANNOT_REMOVE_OWNER(ErrorCategory.VALIDATION),
    INVITATION_NOT_FOUND(ErrorCategory.NOT_FOUND),
    INVITATION_FORBIDDEN(ErrorCategory.FORBIDDEN),
    INVITATION_NOT_PENDING(ErrorCategory.VALIDATION);

    private final ErrorCategory category;

    AccountErrorCode(ErrorCategory category) {
        this.category = category;
    }

    public ErrorCategory category() {
        return category;
    }
}
