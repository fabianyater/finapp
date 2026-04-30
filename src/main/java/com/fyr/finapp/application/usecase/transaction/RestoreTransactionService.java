package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.RestoreTransactionUseCase;
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

public class RestoreTransactionService implements RestoreTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(RestoreTransactionService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public RestoreTransactionService(
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
    public void restore(String transactionId, String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        log.debug("Restoring transaction id={} accountId={} userId={}", transactionId, accountId, userId.value());

        var accId = AccountId.of(accountId);
        var txnId = TransactionId.of(transactionId);

        var account = accountValidator.getAccountAndValidateAccess(accId, userId);

        var transaction = transactionRepository.findDeletedByIdAndAccountId(txnId, accId)
                .orElseThrow(() -> {
                    log.warn("Deleted transaction not found id={} accountId={} userId={}", transactionId, accountId, userId.value());
                    return new ValidationException(
                            "Deleted transaction not found for id=" + transactionId + " and accountId=" + accountId,
                            TransactionErrorCode.TRANSACTION_NOT_FOUND
                    );
                });

        transaction.restore();
        if (transaction.getType().isTransfer()) {
            if (transaction.getToAccountId() != null) {
                account.debit(transaction.getAmount());
            } else {
                account.credit(transaction.getAmount());
            }
        } else {
            account.applyTransaction(transaction.getType(), transaction.getAmount());
        }

        transactionRepository.save(transaction);
        accountRepository.save(account);

        log.info("Transaction restored id={} accountId={} userId={}", transactionId, accountId, userId.value());
    }
}
