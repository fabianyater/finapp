package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.AccountDetailsUseCase;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

public class AccountDetailsService implements AccountDetailsUseCase {
    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;
    private final AccountValidator accountValidator;

    public AccountDetailsService(IAccountRepository accountRepository,
                                 IAuthenticationRepository authenticationRepository,
                                 AccountValidator accountValidator) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    public AccountDetailsResult getAccountDetails(String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        var account = accountValidator.getAccountAndValidateOwnership(AccountId.of(accountId), userId);

        return new AccountDetailsResult(
                account.getId().value().toString(),
                account.getName().value(),
                account.getType().name(),
                account.getInitialBalance().amount(),
                account.getCurrentBalance().amount(),
                account.getCurrency().code(),
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
