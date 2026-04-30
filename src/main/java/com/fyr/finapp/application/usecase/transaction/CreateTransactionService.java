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
import com.fyr.finapp.domain.model.transaction.exception.TransactionErrorCode;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.application.usecase.notification.BudgetAlertChecker;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class CreateTransactionService implements CreateTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateTransactionService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final ICategoryRepository categoryRepository;
    private final AccountValidator accountValidator;
    private final BudgetAlertChecker budgetAlertChecker;

    public CreateTransactionService(IAuthenticationRepository authenticationRepository,
                                    ITransactionRepository transactionRepository,
                                    IAccountRepository accountRepository,
                                    ICategoryRepository categoryRepository,
                                    AccountValidator accountValidator,
                                    BudgetAlertChecker budgetAlertChecker) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.accountValidator = accountValidator;
        this.budgetAlertChecker = budgetAlertChecker;
    }

    @Override
    @Transactional
    public Result create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        log.debug("Creating transaction for userId={} accountId={}", userId.value(), command.accountId());

        var accountId = AccountId.of(command.accountId());
        var categoryId = CategoryId.of(command.categoryId());
        var type = TransactionType.fromString(command.type());

        if (type.isTransfer()) {
            throw new ValidationException(
                    "Use POST /transfers to create transfer transactions",
                    TransactionErrorCode.INVALID_TYPE
            );
        }

        var account = accountValidator.getAccountAndValidateAccess(accountId, userId);
        validateAccountNotArchived(account);

        var category = getCategoryAndValidateOwnership(categoryId, userId);
        validateCategoryType(type, category);

        var amount = Money.of(command.amount(), account.getCurrency().code());

        if (type == TransactionType.EXPENSE &&
                account.getCurrentBalance().subtract(amount).isNegative()) {
            log.warn("Insufficient funds for userId={} accountId={} amount={}",
                    userId.value(), accountId.value(), command.amount());
            throw new ValidationException(
                    "Insufficient funds",
                    AccountErrorCode.INSUFFICIENT_FUNDS
            );
        }

        var transaction = Transaction.create(
                type,
                amount,
                command.description(),
                command.note(),
                Instant.parse(command.occurredOn()),
                userId,
                categoryId,
                accountId,
                null,
                command.tags()
        );

        account.applyTransaction(type, amount);

        transactionRepository.save(transaction);
        accountRepository.save(account);

        log.info("Transaction created id={} type={} amount={} accountId={} userId={}",
                transaction.getId().value(), type, command.amount(), accountId.value(), userId.value());

        if (type == TransactionType.EXPENSE) {
            try {
                budgetAlertChecker.check(userId.value(), categoryId.value(), category.getName().value());
            } catch (Exception e) {
                log.warn("Failed to check budget alert for transaction {}", transaction.getId().value(), e);
            }
        }

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
