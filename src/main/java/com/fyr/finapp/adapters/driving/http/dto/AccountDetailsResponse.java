package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.account.AccountDetailsUseCase;

import java.time.Instant;

public record AccountDetailsResponse(
        String id,
        String name,
        String type,
        long initialBalance,
        long currentBalance,
        String currency,
        String icon,
        String color,
        boolean isDefault,
        boolean isArchived,
        boolean excludeFromTotal,
        Instant createdAt,
        Instant updatedAt) {
    public static AccountDetailsResponse from(AccountDetailsUseCase.AccountDetailsResult account) {
        return new AccountDetailsResponse(
                account.id(),
                account.name(),
                account.type(),
                account.initialBalance(),
                account.currentBalance(),
                account.currency(),
                account.icon(),
                account.color(),
                account.isDefault(),
                account.isArchived(),
                account.excludeFromTotal(),
                account.createdAt(),
                account.updatedAt()
        );
    }
}
