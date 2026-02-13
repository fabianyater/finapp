package com.fyr.finapp.domain.model.transaction;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.transaction.exception.TransactionErrorCode;

import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        if (value == null) {
            throw new ValidationException(
                    "Transaction ID cannot be null",
                    TransactionErrorCode.ID_REQUIRED
            );
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(String id) {
        try {
            return new TransactionId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid Transaction ID format: " + id, TransactionErrorCode.ID_INVALID);
        }
    }
}
