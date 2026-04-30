package com.fyr.finapp.domain.api.recurring;

public interface UpdateRecurringTransactionUseCase {
    void update(Command command);

    record Command(
            String id,
            String accountId,
            String categoryId,
            String type,
            Long amount,
            String description,
            String note,
            String frequency,
            String nextDueDate
    ) {}
}
