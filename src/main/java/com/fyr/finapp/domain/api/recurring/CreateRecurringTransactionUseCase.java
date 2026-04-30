package com.fyr.finapp.domain.api.recurring;

public interface CreateRecurringTransactionUseCase {
    Result create(Command command);

    record Command(
            String accountId,
            String toAccountId,
            String categoryId,
            String type,
            Long amount,
            String description,
            String note,
            String frequency,
            String nextDueDate
    ) {}

    record Result(String id) {}
}
