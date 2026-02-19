package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.TransactionDetailsUseCase;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;

public class TransactionDetailsService implements TransactionDetailsUseCase {
    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final ICategoryRepository categoryRepository;
    private final AccountValidator accountValidator;

    public TransactionDetailsService(IAuthenticationRepository authenticationRepository,
                                     ITransactionRepository transactionRepository,
                                     ICategoryRepository categoryRepository,
                                     AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    public TransactionDetailsResult getTransactionDetails(String transactionId, String accountId) {
        var accId = AccountId.of(accountId);
        var txnId = TransactionId.of(transactionId);
        var userId = authenticationRepository.getCurrentUserId();

        accountValidator.getAccountAndValidateOwnership(accId, userId);

        var transaction = transactionRepository.getTransactionByIdAndAccountId(txnId, accId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        var categoryName = categoryRepository.findById(transaction.getCategoryId())
                .map(Category::getName)
                .orElse(CategoryName.of("Unknown"));

        return new TransactionDetailsResult(
                transaction.getId().value().toString(),
                transaction.getType().name(),
                transaction.getAmount().amount(),
                transaction.getDescription(),
                transaction.getNote(),
                transaction.getOccurredOn().toString(),
                categoryName.value()
        );
    }
}
