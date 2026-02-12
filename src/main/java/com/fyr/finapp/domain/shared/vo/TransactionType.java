package com.fyr.finapp.domain.shared.vo;

import com.fyr.finapp.domain.exception.ValidationException;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public static TransactionType fromString(String value) {
        try {
            return TransactionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid category type: " + value + ". Must be EXPENSE or INCOME",
                    null //TODO: Define error code (category or transaction)
            );
        }
    }

    public boolean isExpense() {
        return this == EXPENSE;
    }

    public boolean isIncome() {
        return this == INCOME;
    }
}
