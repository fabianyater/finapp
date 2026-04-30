package com.fyr.finapp.domain.api.account;

public interface UnarchiveAccountUseCase {
    void unarchive(Command command);

    record Command(String accountId) {
    }
}
