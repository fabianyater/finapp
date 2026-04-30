package com.fyr.finapp.domain.api.account;

public interface ArchiveAccountUseCase {
    void archive(Command command);

    record Command(String accountId, boolean excludeFromTotal) {
    }
}
