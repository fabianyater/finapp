package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.ListAccountsUseCase;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

public class ListAccountsService implements ListAccountsUseCase {
    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;

    public ListAccountsService(IAccountRepository accountRepository, IAuthenticationRepository authenticationRepository) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public PagedAccountResult execute(AccountQuery query) {
        var userId = authenticationRepository.getCurrentUserId();

        var filters = new IAccountRepository.AccountFilters(
                query.page(),
                query.size(),
                query.sortBy(),
                query.direction() == SortDirection.ASC,
                query.types(),
                query.search(),
                query.createdAfter(),
                query.createdBefore()
        );

        var pagedAccounts = accountRepository.findByUserId(userId, filters);

        var accountResults = pagedAccounts.accounts().stream()
                .map(account -> new AccountResult(
                        account.getId().value().toString(),
                        account.getName().value(),
                        account.getType().name(),
                        account.getInitialBalance().amount(),
                        account.getInitialBalance().currency().code(),
                        account.getIcon().name(),
                        account.getColor().value(),
                        account.isDefaultAccount(),
                        account.isArchived(),
                        account.isExcludeFromTotal(),
                        account.getCreatedAt(),
                        account.getUpdatedAt()))
                .toList();

        int totalPages = (int) Math.ceil((double) pagedAccounts.totalElements() / query.size());

        return new PagedAccountResult(
                accountResults,
                query.page(),
                query.size(),
                pagedAccounts.totalElements(),
                totalPages,
                query.page() + 1 < totalPages,
                query.page() > 0,
                query
        );
    }
}
