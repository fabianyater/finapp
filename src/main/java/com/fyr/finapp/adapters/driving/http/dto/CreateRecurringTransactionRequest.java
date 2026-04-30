package com.fyr.finapp.adapters.driving.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateRecurringTransactionRequest(
        @NotBlank String accountId,
        String toAccountId,
        String categoryId,
        @NotBlank String type,
        @NotNull @Positive Long amount,
        @NotBlank String description,
        String note,
        @NotBlank String frequency,
        @NotBlank String nextDueDate
) {}
