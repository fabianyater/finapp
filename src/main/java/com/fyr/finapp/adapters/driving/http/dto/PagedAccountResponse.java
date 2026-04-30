package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.account.ListAccountsUseCase.AccountResult;
import com.fyr.finapp.domain.shared.pagination.PagedResult;

import java.time.Instant;
import java.util.List;

public record PagedAccountResponse(
        List<AccountDto> data,
        PaginationMeta meta
) {
    public static PagedAccountResponse from(PagedResult<AccountResult> result) {
        return new PagedAccountResponse(
                result.content().stream()
                        .map(AccountDto::from)
                        .toList(),
                new PaginationMeta(
                        result.currentPage(),
                        result.pageSize(),
                        result.totalElements(),
                        result.totalPages(),
                        result.hasNext(),
                        result.hasPrevious()
                )
        );
    }

    public record AccountDto(
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
            Instant updatedAt
    ) {
        public static AccountDto from(AccountResult account) {
            return new AccountDto(
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
}