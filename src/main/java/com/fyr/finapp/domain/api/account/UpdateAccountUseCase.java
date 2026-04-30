package com.fyr.finapp.domain.api.account;

public interface UpdateAccountUseCase {
    void update(Command command);

    record Command(
            String accountId,
            String name,
            String type,
            Long initialBalance,
            String icon,
            String color,
            boolean defaultAccount,
            boolean excludeFromTotal
    ) {}
}
