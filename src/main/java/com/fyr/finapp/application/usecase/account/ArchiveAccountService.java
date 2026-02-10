package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.ArchiveAccountUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import jakarta.transaction.Transactional;

import java.util.Comparator;

public class ArchiveAccountService implements ArchiveAccountUseCase {
    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;
    private final AccountValidator accountValidator;

    public ArchiveAccountService(IAccountRepository accountRepository,
                                 IAuthenticationRepository authenticationRepository,
                                 AccountValidator accountValidator) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public void archive(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var accountId = AccountId.of(command.accountId());

        Account existingAccount = accountValidator.getAccountAndValidateOwnership(accountId, userId);

        validateCanArchive(existingAccount);
        boolean wasDefault = existingAccount.isDefaultAccount();

        existingAccount.archive();

        if (wasDefault) {
            existingAccount.unmarkAsDefault();
        }

        if (command.excludeFromTotal() && !existingAccount.isExcludeFromTotal()) {
            existingAccount.excludeFromTotal();
        }

        accountRepository.save(existingAccount);

        if (wasDefault) {
            assignNextDefaultAccount(userId);
        }
    }

    private void validateCanArchive(Account account) {
        if (account.isArchived()) {
            throw new ValidationException(
                    "Account is already archived",
                    AccountErrorCode.ACCOUNT_ALREADY_ARCHIVED
            );
        }

        // TODO: validateNoActiveTransactions(account);
    }

    private void assignNextDefaultAccount(UserId userId) {
        accountRepository.findAllByUserId(userId)
                .stream()
                .filter(acc -> !acc.isArchived())
                .max(Comparator.comparing(Account::getCreatedAt))
                .ifPresent(account -> {
                    account.markAsDefault();
                    accountRepository.save(account);
                });
    }
}
