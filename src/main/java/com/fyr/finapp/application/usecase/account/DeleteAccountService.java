package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.DeleteAccountUseCase;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

public class DeleteAccountService implements DeleteAccountUseCase {

    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;
    private final AccountValidator accountValidator;

    public DeleteAccountService(IAccountRepository accountRepository,
                                IAuthenticationRepository authenticationRepository,
                                AccountValidator accountValidator) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    public void delete(String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        var accId = AccountId.of(accountId);

        var account = accountValidator.getAccountAndValidateOwnership(accId, userId);

        accountRepository.delete(accId);
    }
}
