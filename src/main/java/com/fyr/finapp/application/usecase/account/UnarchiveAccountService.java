package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.UnarchiveAccountUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import jakarta.transaction.Transactional;

public class UnarchiveAccountService implements UnarchiveAccountUseCase {

    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;
    private final AccountValidator accountValidator;

    public UnarchiveAccountService(IAccountRepository accountRepository,
                                   IAuthenticationRepository authenticationRepository,
                                   AccountValidator accountValidator) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public void unarchive(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var accountId = AccountId.of(command.accountId());

        Account account = accountValidator.getAccountAndValidateOwnership(accountId, userId);

        if (!account.isArchived()) {
            throw new ValidationException(
                    "Account is not archived",
                    AccountErrorCode.ACCOUNT_NOT_ARCHIVED
            );
        }

        account.unarchive();
        accountRepository.save(account);
    }
}
