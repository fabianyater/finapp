package com.fyr.finapp.domain.api.transaction;

import java.util.List;

public interface TransactionDetailsUseCase {
    TransactionDetailsResult getTransactionDetails(String transactionId, String accountId);

    record TransactionDetailsResult(
            String id,
            String type,
            Long amount,
            String description,
            String note,
            String occurredOn,
            String categoryName,
            List<String> tags) {
    }
}
