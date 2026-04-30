package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.transaction.TransactionDetailsUseCase;

import java.util.List;

public record TransactionResponse(
        String id,
        String type,
        long amount,
        String description,
        String note,
        String occurredOn,
        String categoryName,
        List<String> tags) {
    public static TransactionResponse from(TransactionDetailsUseCase.TransactionDetailsResult transactionDetailsResult) {
        return new TransactionResponse(
                transactionDetailsResult.id(),
                transactionDetailsResult.type(),
                transactionDetailsResult.amount(),
                transactionDetailsResult.description(),
                transactionDetailsResult.note(),
                transactionDetailsResult.occurredOn(),
                transactionDetailsResult.categoryName(),
                transactionDetailsResult.tags()
        );
    }
}
