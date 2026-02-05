package com.fyr.finapp.domain.model.account.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;

import static com.fyr.finapp.domain.model.account.AccountConstraints.ACCOUNT_NAME_MAX_LENGTH;

public record AccountName(String value) {
    public AccountName {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Account name cannot be null or empty", AccountErrorCode.NAME_REQUIRED);
        }

        String trimmed = value.trim();
        if (trimmed.length() > ACCOUNT_NAME_MAX_LENGTH) {
            throw new ValidationException(
                    "Account name cannot exceed " + ACCOUNT_NAME_MAX_LENGTH + " characters",
                    AccountErrorCode.NAME_TOO_LONG
            );
        }

        value = trimmed;
    }

    public static AccountName of(String name) {
        return new AccountName(name);
    }
}
