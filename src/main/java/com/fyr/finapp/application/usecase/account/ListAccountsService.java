package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.ListAccountsUseCase;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.shared.pagination.PagedResult;
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
    public PagedResult<AccountResult> execute(AccountQuery query) {
        var userId = authenticationRepository.getCurrentUserId();
        var filters = mapToFilters(query);
        var pagedAccounts = accountRepository.findByUserId(userId, filters);

        var accountResults = pagedAccounts.accounts().stream()
                .map(this::mapToResult)
                .toList();

        int totalPages = (int) Math.ceil((double) pagedAccounts.totalElements() / query.pageRequest().size());

        return new PagedResult<>(
                accountResults,
                query.pageRequest().page(),
                query.pageRequest().size(),
                pagedAccounts.totalElements(),
                totalPages,
                query.pageRequest().page() + 1 < totalPages,
                query.pageRequest().page() > 0
        );
    }

    private IAccountRepository.AccountFilters mapToFilters(AccountQuery query) {
        return new IAccountRepository.AccountFilters(
                query.pageRequest(),
                query.types(),
                query.search(),
                query.createdAfter(),
                query.createdBefore()
        );
    }

    private AccountResult mapToResult(Account account) {
        return new AccountResult(
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
                account.getUpdatedAt()
        );
    }
}
