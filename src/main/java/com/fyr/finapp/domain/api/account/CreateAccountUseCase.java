package com.fyr.finapp.domain.api.account;

public interface CreateAccountUseCase {
    Result create(Command command);

    record Command(String name,
                   String type,
                   Long initialBalance,
                   String icon,
                   String color,
                   String currency) {
    }

    record Result(String id) {
    }
}
