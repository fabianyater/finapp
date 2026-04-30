package com.fyr.finapp.domain.api.transaction;

import java.util.List;

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
            String categoryId,
            List<String> tags) {
    }
}
