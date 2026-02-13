package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.CreateTransactionUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;

import java.time.Instant;

public class CreateTransactionService implements CreateTransactionUseCase {
    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final ICategoryRepository categoryRepository;
    private final AccountValidator accountValidator;

    public CreateTransactionService(IAuthenticationRepository authenticationRepository,
                                    ITransactionRepository transactionRepository,
                                    IAccountRepository accountRepository,
                                    ICategoryRepository categoryRepository,
                                    AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public Result create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var accountId = AccountId.of(command.accountId());
        var categoryId = CategoryId.of(command.categoryId());
        var type = TransactionType.fromString(command.type());

        var existingAccount = accountValidator.getAccountAndValidateOwnership(accountId, userId);
        validateAccountNotArchived(existingAccount);

        var category = getCategoryAndValidateOwnership(categoryId, userId);
        validateCategoryType(type, category);

        var transaction = Transaction.create(
                type,
                Money.of(command.amount(), existingAccount.getCurrency().code()),
                command.description(),
                command.note(),
                Instant.parse(command.occurredOn()),
                userId,
                categoryId,
                accountId
        );

        transactionRepository.save(transaction);

        return new Result(transaction.getId().value().toString());
    }

    private void validateAccountNotArchived(Account account) {
        if (account.isArchived()) {
            throw new ValidationException(
                    "Cannot create transaction in archived account",
                    AccountErrorCode.ACCOUNT_ARCHIVED
            );
        }
    }

    public @NonNull Category getCategoryAndValidateOwnership(CategoryId categoryId, UserId userId) {
        Category category = getCategoryOrThrow(categoryId);
        validateOwnership(category, userId);

        return category;
    }

    private Category getCategoryOrThrow(CategoryId categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ValidationException(
                        "Category not found",
                        CategoryErrorCode.CATEGORY_NOT_FOUND
                ));
    }

    public void validateOwnership(Category category, UserId userId) {
        if (!category.getUserId().equals(userId)) {
            throw new ForbiddenException(
                    "You don't have access to this category",
                    CategoryErrorCode.ACCESS_DENIED
            );
        }
    }

    private void validateCategoryType(TransactionType transactionType, Category category) {
        if (!category.getType().equals(transactionType)) {
            throw new ValidationException(
                    "Category type does not match transaction type",
                    CategoryErrorCode.CATEGORY_TYPE_MISMATCH
            );
        }
    }
}
