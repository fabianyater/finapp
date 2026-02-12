package com.fyr.finapp.application.usecase.account;

import com.fyr.finapp.domain.api.account.CreateAccountUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.shared.vo.Currency;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;

import java.util.List;

public class AccountService implements CreateAccountUseCase {
    private final IAccountRepository accountRepository;
    private final IAuthenticationRepository authenticationRepository;

    public AccountService(IAccountRepository accountRepository, IAuthenticationRepository authenticationRepository) {
        this.accountRepository = accountRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public Result create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var account = createAccount(command, userId);

        validateInitialBalanceForType(account.getType(), account.getInitialBalance());

        if (accountRepository.existsByUserIdAndName(userId, account.getName())) {
            throw new ValidationException(
                    "An account with the name '" + account.getName().value() + "' already exists.",
                    AccountErrorCode.NAME_ALREADY_EXISTS
            );

        }

        //TODO: This is a workaround to ensure that the first account created for a user is marked as default. We should have a better way to handle this in the future.
        List<Account> existingAccounts = accountRepository.findAllByUserId(userId);

        if (existingAccounts.isEmpty()) account.markAsDefault();

        accountRepository.save(account);

        return new Result(account.getId().value().toString());
    }

    private Account createAccount(Command command, UserId userId) {
        AccountName name = new AccountName(command.name());
        AccountType type = AccountType.fromString(command.type());
        Currency currency = Currency.of(command.currency());
        Money initialBalance = Money.of(command.initialBalance(), currency);

        return Account.create(userId, name, type, initialBalance);
    }

    private void validateInitialBalanceForType(AccountType type, Money initialBalance) {
        if ((type == AccountType.CASH || type == AccountType.BANK)
                && initialBalance.isNegative()) {
            throw new ValidationException(
                    type.name() + " accounts should not have negative initial balance",
                    AccountErrorCode.INVALID_INITIAL_BALANCE
            );
        }
    }
}
