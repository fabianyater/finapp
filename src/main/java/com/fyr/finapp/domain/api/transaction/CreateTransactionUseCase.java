package com.fyr.finapp.domain.api.transaction;

public interface CreateTransactionUseCase {
    Result create(Command command);

    record Command(
            String type,
            Long amount,
            String description,
            String note,
            String occurredOn,
            String categoryId,
            String accountId) {
    }

    record Result(String id) {
    }
}
