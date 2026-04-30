package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.transaction.ListDeletedTransactionsUseCase;

public record DeletedTransactionResponse(
        String id,
        String type,
        long amount,
        String description,
        String note,
        String occurredOn,
        String deletedAt,
        String categoryId,
        String accountId) {

    public static DeletedTransactionResponse from(ListDeletedTransactionsUseCase.Result r) {
        return new DeletedTransactionResponse(
                r.id(),
                r.type(),
                r.amount(),
                r.description(),
                r.note(),
                r.occurredOn(),
                r.deletedAt(),
                r.categoryId(),
                r.accountId()
        );
    }
}
