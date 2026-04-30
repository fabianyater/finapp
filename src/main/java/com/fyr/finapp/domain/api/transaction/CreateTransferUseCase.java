package com.fyr.finapp.domain.api.transaction;

public interface CreateTransferUseCase {
    Result create(Command command);

    record Command(
            String fromAccountId,
            String toAccountId,
            Long amount,
            String description,
            String note,
            String occurredOn) {
    }

    record Result(String outTransactionId, String inTransactionId) {
    }
}
