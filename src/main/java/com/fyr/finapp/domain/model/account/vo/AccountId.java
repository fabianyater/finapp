package com.fyr.finapp.domain.model.account.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;

import java.util.UUID;

public record AccountId(UUID value) {
    public AccountId {
        if (value == null) throw new ValidationException(
                "Account ID cannot be null",
                AccountErrorCode.ID_REQUIRED
        );
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId of(String id) {
        try {
            return new AccountId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid Account ID format: " + id, AccountErrorCode.ID_INVALID);
        }
    }
}
