package com.fyr.finapp.adapters.driving.http.dto;

import java.util.List;

public record CreateTransactionRequest(
        String type,
        Long amount,
        String description,
        String note,
        String occurredOn,
        String categoryId,
        List<String> tags) {
}
