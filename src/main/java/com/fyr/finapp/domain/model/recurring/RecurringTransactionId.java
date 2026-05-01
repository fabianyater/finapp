package com.fyr.finapp.domain.model.recurring;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.recurring.exception.RecurringTransactionErrorCode;

import java.util.UUID;

public record RecurringTransactionId(UUID value) {
    public RecurringTransactionId {
        if (value == null) {
            throw new ValidationException("Recurring transaction ID cannot be null", RecurringTransactionErrorCode.ID_REQUIRED);
        }
    }

    public static RecurringTransactionId generate() {
        return new RecurringTransactionId(UUID.randomUUID());
    }

    public static RecurringTransactionId of(String id) {
        try {
            return new RecurringTransactionId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid recurring transaction ID format: " + id, RecurringTransactionErrorCode.ID_INVALID);
        }
    }
}
