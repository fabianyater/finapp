package com.fyr.finapp.adapters.driving.http.dto;

public record UpdateTransactionRequest(
        String type,
        Long amount,
        String description,
        String note,
        String occurredOn,
        String categoryId,
        String accountId) {
}
