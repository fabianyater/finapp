package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.recurring.UpdateRecurringTransactionUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.model.recurring.exception.RecurringTransactionErrorCode;
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
                .orElseThrow(() -> new NotFoundException("Recurring transaction not found", RecurringTransactionErrorCode.NOT_FOUND));

        if (!recurring.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied", RecurringTransactionErrorCode.ACCESS_DENIED);
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
                    .orElseThrow(() -> new NotFoundException("Category not found", CategoryErrorCode.CATEGORY_NOT_FOUND));
            if (!category.getUserId().equals(userId)) {
                throw new ForbiddenException("Access denied to this category", CategoryErrorCode.ACCESS_DENIED);
            }
        }

        var amount = Money.of(command.amount(), account.getCurrency().code());

        recurring.update(accountId, categoryId, type, amount, command.description(), command.note(), frequency, nextDueDate);
        recurringTransactionRepository.save(recurring);
    }
}
