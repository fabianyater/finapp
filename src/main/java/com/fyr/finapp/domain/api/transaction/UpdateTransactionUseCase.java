package com.fyr.finapp.domain.api.transaction;

public interface UpdateTransactionUseCase {
    void update(Command command);

    record Command(
            String transactionId,
            String type,
            Long amount,
            String description,
            String note,
            String occurredOn,
            String accountId,
            String categoryId) {
    }
}
