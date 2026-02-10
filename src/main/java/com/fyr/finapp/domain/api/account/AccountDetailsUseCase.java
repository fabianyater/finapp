package com.fyr.finapp.domain.api.account;

import java.time.Instant;

public interface AccountDetailsUseCase {
    AccountDetailsResult getAccountDetails(String accountId);

    record AccountDetailsResult(
            String id,
            String name,
            String type,
            long initialBalance,
            String currency,
            String icon,
            String color,
            boolean isDefault,
            boolean isArchived,
            boolean excludeFromTotal,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
