package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.UpdateTransactionUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class UpdateTransactionService implements UpdateTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(UpdateTransactionService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final ICategoryRepository categoryRepository;
    private final AccountValidator accountValidator;
    private final BudgetAlertChecker budgetAlertChecker;

    public UpdateTransactionService(IAuthenticationRepository authenticationRepository,
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
    public void update(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var transactionId = TransactionId.of(command.transactionId());
        log.debug("Updating transaction id={} userId={}", command.transactionId(), userId.value());

        var transaction = getTransactionAndValidateAccess(transactionId, userId);

        if (transaction.getType().isTransfer()) {
            throw new ValidationException(
                    "Transfer transactions cannot be edited",
                    TransactionErrorCode.INVALID_TYPE
            );
        }

        var originalState = captureOriginalTransactionState(transaction);
        var newState = prepareNewTransactionState(command, userId);

        validateTransactionUpdate(originalState, newState);
        validateSufficientFundsBeforeMutation(originalState, newState);

        applyAccountBalanceChanges(originalState, newState);
        updateTransaction(transaction, command, newState);

        saveAccounts(originalState, newState);
        transactionRepository.save(transaction);

        log.info("Transaction updated id={} userId={} type={} amount={} accountId={}",
                command.transactionId(), userId.value(), command.type(), command.amount(), command.accountId());

        if (newState.type() == TransactionType.EXPENSE) {
            try {
                budgetAlertChecker.check(userId.value(), newState.categoryId().value(), newState.category().getName().value());
            } catch (Exception e) {
                log.warn("Failed to check budget alert after update for transaction {}", command.transactionId(), e);
            }
        }
    }

    private OriginalTransactionState captureOriginalTransactionState(Transaction transaction) {
        var accountId = transaction.getAccountId();

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ValidationException(
                        "Old account not found",
                        AccountErrorCode.ACCOUNT_NOT_FOUND
                ));

        return new OriginalTransactionState(
                accountId,
                transaction.getType(),
                transaction.getAmount(),
                account
        );
    }

    private NewTransactionState prepareNewTransactionState(Command command, UserId userId) {
        var accountId = AccountId.of(command.accountId());
        var type = TransactionType.fromString(command.type());
        var categoryId = CategoryId.of(command.categoryId());

        var account = accountValidator.getAccountAndValidateAccess(accountId, userId);
        accountValidator.validateAccountNotArchived(account);

        var category = getCategoryAndValidateOwnership(categoryId, userId);
        var amount = Money.of(command.amount(), account.getCurrency().code());

        return new NewTransactionState(accountId, type, amount, account, category, categoryId);
    }

    private void validateTransactionUpdate(
            OriginalTransactionState original,
            NewTransactionState newState) {

        if (!original.accountId().equals(newState.accountId())) {
            accountValidator.validateSameCurrency(original.account(), newState.account());
        }

        validateCategoryType(newState.type(), newState.category());
    }

    private void validateSufficientFundsBeforeMutation(
            OriginalTransactionState original,
            NewTransactionState newState) {

        if (newState.type() != TransactionType.EXPENSE) {
            return;
        }

        boolean isSameAccount = original.accountId().equals(newState.accountId());

        if (isSameAccount) {
            boolean hasFunds = original.account().hasSufficientFundsForUpdate(
                    original.type(),
                    original.amount(),
                    newState.amount()
            );

            if (!hasFunds) {
                log.warn("Insufficient funds for update transactionId={} accountId={} userId={}",
                        original.accountId().value(), newState.amount(), original.account.getUserId());

                throw new ValidationException(
                        "Insufficient funds in account",
                        AccountErrorCode.INSUFFICIENT_FUNDS
                );
            }
        } else {
            boolean hasFunds = newState.account().hasSufficientFundsForExpense(newState.amount());

            if (!hasFunds) {
                log.warn("Insufficient funds in target account accountId={} amount={}",
                        newState.accountId().value(), newState.amount());

                throw new ValidationException(
                        "Insufficient funds in target account",
                        AccountErrorCode.INSUFFICIENT_FUNDS
                );
            }
        }
    }

    private void applyAccountBalanceChanges(
            OriginalTransactionState original,
            NewTransactionState newState) {

        boolean isSameAccount = original.accountId().equals(newState.accountId());

        if (isSameAccount) {
            Account account = original.account();
            account.reverseTransaction(original.type(), original.amount());
            account.applyTransaction(newState.type(), newState.amount());
        } else {
            original.account().reverseTransaction(original.type(), original.amount());
            newState.account().applyTransaction(newState.type(), newState.amount());
        }
    }


    private void updateTransaction(Transaction transaction, Command command, NewTransactionState newState) {
        Instant occurredOn = Instant.parse(command.occurredOn());
        transaction.update(
                newState.type(),
                newState.amount(),
                command.description(),
                command.note(),
                occurredOn,
                newState.categoryId(),
                newState.accountId(),
                command.tags()
        );
    }

    private void saveAccounts(OriginalTransactionState original, NewTransactionState newState) {
        boolean isSameAccount = original.accountId().equals(newState.accountId());

        if (isSameAccount) {
            accountRepository.save(original.account());
        } else {
            accountRepository.save(original.account());
            accountRepository.save(newState.account());
        }
    }

    private Transaction getTransactionAndValidateAccess(TransactionId transactionId, UserId userId) {
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found id={} userId={}", transactionId.value(), userId.value());

                    return new ValidationException(
                            "Transaction not found",
                            TransactionErrorCode.TRANSACTION_NOT_FOUND
                    );
                });

        if (!transaction.getUserId().equals(userId)) {
            // Not the direct owner — verify account access (throws ForbiddenException if denied)
            accountValidator.getAccountAndValidateAccess(transaction.getAccountId(), userId);
        }

        return transaction;
    }

    private Category getCategoryAndValidateOwnership(CategoryId categoryId, UserId userId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ValidationException(
                        "Category not found",
                        CategoryErrorCode.CATEGORY_NOT_FOUND
                ));

        if (!category.getUserId().equals(userId)) {
            throw new ForbiddenException(
                    "You don't have access to this category",
                    CategoryErrorCode.ACCESS_DENIED
            );
        }

        return category;
    }

    private void validateCategoryType(TransactionType transactionType, Category category) {
        if (!category.getType().equals(transactionType)) {
            throw new ValidationException(
                    "Category type does not match transaction type",
                    CategoryErrorCode.CATEGORY_TYPE_MISMATCH
            );
        }
    }

    private record OriginalTransactionState(
            AccountId accountId,
            TransactionType type,
            Money amount,
            Account account
    ) {
    }

    private record NewTransactionState(
            AccountId accountId,
            TransactionType type,
            Money amount,
            Account account,
            Category category,
            CategoryId categoryId
    ) {
    }
}
