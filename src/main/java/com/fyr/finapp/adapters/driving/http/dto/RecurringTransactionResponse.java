package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.model.recurring.RecurringTransaction;

public record RecurringTransactionResponse(
        String id,
        String accountId,
        String toAccountId,
        String categoryId,
        String type,
        Long amount,
        String currency,
        String description,
        String note,
        String frequency,
        String nextDueDate,
        String lastGeneratedAt,
        boolean active,
        String createdAt,
        String updatedAt
) {
    public static RecurringTransactionResponse from(RecurringTransaction rt) {
        return new RecurringTransactionResponse(
                rt.getId().value().toString(),
                rt.getAccountId().value().toString(),
                rt.getToAccountId() != null ? rt.getToAccountId().value().toString() : null,
                rt.getCategoryId() != null ? rt.getCategoryId().value().toString() : null,
                rt.getType().name(),
                rt.getAmount().amount(),
                rt.getAmount().currency().code(),
                rt.getDescription(),
                rt.getNote(),
                rt.getFrequency().name(),
                rt.getNextDueDate().toString(),
                rt.getLastGeneratedAt() != null ? rt.getLastGeneratedAt().toString() : null,
                rt.isActive(),
                rt.getCreatedAt().toString(),
                rt.getUpdatedAt().toString()
        );
    }
}
