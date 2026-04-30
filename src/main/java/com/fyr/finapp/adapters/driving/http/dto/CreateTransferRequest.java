package com.fyr.finapp.adapters.driving.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransferRequest(
        @NotBlank String fromAccountId,
        @NotBlank String toAccountId,
        @NotNull @Positive Long amount,
        String description,
        String note,
        @NotBlank String occurredOn
) {
}
