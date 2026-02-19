package com.fyr.finapp.domain.api.transaction;

public interface TransactionDetailsUseCase {
    TransactionDetailsResult getTransactionDetails(String transactionId, String accountId);

    record TransactionDetailsResult(
            String id,
            String type,
            Long amount,
            String description,
            String note,
            String occurredOn,
            String categoryName) {
    }
}
