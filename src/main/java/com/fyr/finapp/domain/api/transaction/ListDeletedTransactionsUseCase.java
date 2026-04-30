package com.fyr.finapp.domain.api.transaction;

import java.util.List;

public interface ListDeletedTransactionsUseCase {
    List<Result> execute(String accountId);

    record Result(String id, String type, long amount, String description, String note,
                  String occurredOn, String deletedAt, String categoryId, String accountId) {}
}
