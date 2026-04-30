package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.recurring.UpdateRecurringTransactionUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.RecurringFrequency;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

public class UpdateRecurringTransactionService implements UpdateRecurringTransactionUseCase {
    private final IAuthenticationRepository authenticationRepository;
    private final IRecurringTransactionRepository recurringTransactionRepository;
    private final IAccountRepository accountRepository;
    private final ICategoryRepository categoryRepository;
    private final AccountValidator accountValidator;

    public UpdateRecurringTransactionService(
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
    public void update(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var id = RecurringTransactionId.of(command.id());

        var recurring = recurringTransactionRepository.findById(id, userId)
                .orElseThrow(() -> new ValidationException("Recurring transaction not found", null));

        if (!recurring.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied", null);
        }

        var accountId = AccountId.of(command.accountId());
        var account = accountValidator.getAccountAndValidateOwnership(accountId, userId);
        var type = TransactionType.fromString(command.type());
        var frequency = RecurringFrequency.fromString(command.frequency());
        var nextDueDate = LocalDate.parse(command.nextDueDate());

        CategoryId categoryId = null;
        if (command.categoryId() != null && !command.categoryId().isBlank()) {
            categoryId = CategoryId.of(command.categoryId());
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ValidationException("Category not found", null));
            if (!category.getUserId().equals(userId)) {
                throw new ValidationException("Access denied to category", null);
            }
        }

        var amount = Money.of(command.amount(), account.getCurrency().code());

        recurring.update(accountId, categoryId, type, amount, command.description(), command.note(), frequency, nextDueDate);
        recurringTransactionRepository.save(recurring);
    }
}
