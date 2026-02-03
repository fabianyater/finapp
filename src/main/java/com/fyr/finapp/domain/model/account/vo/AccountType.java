package com.fyr.finapp.domain.model.account.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;

public enum AccountType {
    CASH("Efectivo"),
    BANK("Banco"),
    CARD("Tarjeta");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AccountType fromString(String type) {
        try {
            return AccountType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid account type: " + type + ". Valid types: CASH, BANK, CARD",
                    AccountErrorCode.NAME_TOO_LONG
            );
        }
    }
}
