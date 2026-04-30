package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.DeleteTransactionUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.model.transaction.exception.TransactionErrorCode;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTransactionService implements DeleteTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(DeleteTransactionService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public DeleteTransactionService(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountValidator = accountValidator;
    }

    @Transactional
    @Override
    public void delete(String transactionId, String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        log.debug("Deleting transaction id={} accountId={} userId={}", transactionId, accountId, userId.value());

        var accId = AccountId.of(accountId);
        var txnId = TransactionId.of(transactionId);

        var account = accountValidator.getAccountAndValidateAccess(accId, userId);

        var transaction = transactionRepository.getTransactionByIdAndAccountId(txnId, accId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found id={} accountId={} userId={}", transactionId, accountId, userId.value());

                    return new ValidationException(
                            "Transaction not found for id=" + transactionId + " and accountId=" + accountId,
                            TransactionErrorCode.TRANSACTION_NOT_FOUND
                    );
                });

        if (transaction.getType().isTransfer()) {
            if (transaction.getToAccountId() != null) {
                // OUT side (debit was applied) — reverse by crediting
                account.credit(transaction.getAmount());
            } else {
                // IN side (credit was applied) — reverse by debiting
                account.debit(transaction.getAmount());
            }
        } else {
            account.reverseTransaction(transaction.getType(), transaction.getAmount());
        }

        transaction.softDelete();

        transactionRepository.save(transaction);
        accountRepository.save(account);

        log.info("Transaction deleted id={} accountId={} userId={}", transactionId, accountId, userId.value());
    }
}
