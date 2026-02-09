package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.UpdateAccountUseCase;
import com.fyr.finapp.domain.common.vo.Color;
import com.fyr.finapp.domain.common.vo.Icon;
import com.fyr.finapp.domain.common.vo.Money;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;

public class UpdateAccountService implements UpdateAccountUseCase {
    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;

    public UpdateAccountService(IAccountRepository accountRepository,
                                IAuthenticationRepository authenticationRepository) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    @Transactional
    public void update(Command command) {
        var accountId = AccountId.of(command.accountId());
        var userId = authenticationRepository.getCurrentUserId();

        Account existingAccount = getAccountOrThrow(accountId);
        validateAccountOwnership(existingAccount, userId);

        var newName = AccountName.of(command.name());
        validateUniqueNameIfChanged(existingAccount, newName, userId);

        handleDefaultAccountChange(command, existingAccount, userId);
        updateExcludeFromTotalIfNeeded(command, existingAccount);

        // TODO: Handle changes to the initial balance once transactions are implemented

        existingAccount.update(
                newName,
                AccountType.valueOf(command.type()),
                Money.of(command.initialBalance(), existingAccount.getCurrency()),
                Icon.of(command.icon()),
                Color.of(command.color())
        );

        accountRepository.save(existingAccount);
    }

    private @NonNull Account getAccountOrThrow(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        "Account not found for id=" + accountId,
                        AccountErrorCode.ACCOUNT_NOT_FOUND
                ));
    }

    private void validateAccountOwnership(Account existingAccount, UserId userId) {
        if (!existingAccount.getUserId().equals(userId)) {
            throw new ForbiddenException(
                    "You don't have access to this account",
                    AccountErrorCode.ACCESS_DENIED
            );
        }
    }

    private void validateUniqueNameIfChanged(Account existingAccount, AccountName newName, UserId userId) {
        if (existingAccount.getName().equals(newName)) return;

        if (accountRepository.existsByUserIdAndName(userId, newName)) {
            throw new ValidationException(
                    "An account with this name already exists",
                    AccountErrorCode.NAME_ALREADY_EXISTS
            );
        }
    }

    private void handleDefaultAccountChange(Command command, Account existingAccount, UserId userId) {
        if (command.defaultAccount() && !existingAccount.isDefaultAccount()) {
            accountRepository.unmarkAllAsDefault(userId);
            existingAccount.markAsDefault();
        } else if (!command.defaultAccount() && existingAccount.isDefaultAccount()) {
            existingAccount.unmarkAsDefault();
        }
    }

    private void updateExcludeFromTotalIfNeeded(Command command, Account existingAccount) {
        if (command.excludeFromTotal() != existingAccount.isExcludeFromTotal()) {
            if (command.excludeFromTotal()) {
                existingAccount.excludeFromTotal();
            } else {
                existingAccount.includeInTotal();
            }
        }
    }
}
