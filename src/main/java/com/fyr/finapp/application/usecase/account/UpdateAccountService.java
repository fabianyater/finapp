package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.UpdateAccountUseCase;
import com.fyr.finapp.domain.shared.vo.Color;
import com.fyr.finapp.domain.shared.vo.Icon;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import jakarta.transaction.Transactional;

public class UpdateAccountService implements UpdateAccountUseCase {
    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;
    private final AccountValidator accountValidator;

    public UpdateAccountService(IAccountRepository accountRepository,
                                IAuthenticationRepository authenticationRepository, AccountValidator accountValidator) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public void update(Command command) {
        var accountId = AccountId.of(command.accountId());
        var userId = authenticationRepository.getCurrentUserId();

        Account existingAccount = accountValidator.getAccountAndValidateOwnership(accountId, userId);

        var newName = AccountName.of(command.name());

        accountValidator.validateUniqueNameForUpdate(existingAccount, newName, userId);

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
