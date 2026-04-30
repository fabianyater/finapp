package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.recurring.CreateRecurringTransactionUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.recurring.RecurringTransaction;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.RecurringFrequency;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class CreateRecurringTransactionService implements CreateRecurringTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateRecurringTransactionService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final IRecurringTransactionRepository recurringTransactionRepository;
    private final IAccountRepository accountRepository;
    private final ICategoryRepository categoryRepository;
    private final AccountValidator accountValidator;

    public CreateRecurringTransactionService(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository,
            IAccountRepository accountRepository,
            ICategoryRepository categoryRepository,
            AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public Result create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var accountId = AccountId.of(command.accountId());
        var type = TransactionType.fromString(command.type());
        var frequency = RecurringFrequency.fromString(command.frequency());
        var nextDueDate = LocalDate.parse(command.nextDueDate());

        var account = accountValidator.getAccountAndValidateOwnership(accountId, userId);

        if (account.isArchived()) {
            throw new ValidationException("Cannot create recurring transaction for archived account", null);
        }

        CategoryId categoryId = null;
        if (command.categoryId() != null && !command.categoryId().isBlank()) {
            categoryId = CategoryId.of(command.categoryId());
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ValidationException("Category not found", null));
            if (!category.getUserId().equals(userId)) {
                throw new ValidationException("Access denied to category", null);
            }
        }

        AccountId toAccountId = null;
        if (command.toAccountId() != null && !command.toAccountId().isBlank()) {
            toAccountId = AccountId.of(command.toAccountId());
        }

        var amount = Money.of(command.amount(), account.getCurrency().code());

        var recurring = RecurringTransaction.create(
                userId, accountId, toAccountId, categoryId,
                type, amount, command.description(), command.note(),
                frequency, nextDueDate
        );

        recurringTransactionRepository.save(recurring);

        return new Result(recurring.getId().value().toString());
    }
}
