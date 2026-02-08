package com.fyr.finapp.domain.spi.account;

import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface IAccountRepository {
    void save(Account account);
    PagedAccounts findByUserId(UserId userId, AccountFilters filters);
    boolean existsByUserIdAndName(UserId userId, AccountName name);

    record AccountFilters(
            int page,
            int size,
            String sortBy,
            boolean ascending,
            Set<String> types,
            String search,
            Instant createdAfter,
            Instant createdBefore
    ) {}

    record PagedAccounts(
            List<Account> accounts,
            long totalElements
    ) {}
}
