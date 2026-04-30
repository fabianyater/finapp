package com.fyr.finapp.adapters.driving.http.dto;


import com.fyr.finapp.domain.api.transaction.ListTransactionUseCase;
import com.fyr.finapp.domain.api.transaction.ListTransactionUseCase.TransactionResult;
import com.fyr.finapp.domain.shared.pagination.PagedResult;

import java.util.List;

public record PagedTransactionResponse(
        List<TransactionDto> data,
        PaginationMeta meta
) {
    public static PagedTransactionResponse from(PagedResult<TransactionResult> result) {
        return new PagedTransactionResponse(
                result.content().stream()
                        .map(TransactionDto::from)
                        .toList(),
                new PaginationMeta(
                        result.currentPage(),
                        result.pageSize(),
                        result.totalElements(),
                        result.totalPages(),
                        result.hasNext(),
                        result.hasPrevious()
                )
        );
    }

    public record TransactionDto(
            String id,
            String type,
            long amount,
            String description,
            String note,
            String occurredOn,
            String accountId,
            String categoryId,
            String categoryName,
            String categoryColor,
            String categoryIcon,
            String toAccountId,
            List<String> tags,
            String createdBy
    ) {
        public static TransactionDto from(ListTransactionUseCase.TransactionResult transaction) {
            return new TransactionDto(
                    transaction.id(),
                    transaction.type(),
                    transaction.amount(),
                    transaction.description(),
                    transaction.note(),
                    transaction.occurredOn(),
                    transaction.accountId(),
                    transaction.categoryId(),
                    transaction.categoryName(),
                    transaction.categoryColor(),
                    transaction.categoryIcon(),
                    transaction.toAccountId(),
                    transaction.tags(),
                    transaction.createdBy()
            );
        }
    }
}
