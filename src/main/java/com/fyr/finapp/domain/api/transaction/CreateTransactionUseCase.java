package com.fyr.finapp.domain.api.transaction;

import java.util.List;

public interface CreateTransactionUseCase {
    Result create(Command command);

    record Command(
            String type,
            Long amount,
            String description,
            String note,
            String occurredOn,
            String categoryId,
            String accountId,
            List<String> tags) {
    }

    record Result(String id) {
    }
}
