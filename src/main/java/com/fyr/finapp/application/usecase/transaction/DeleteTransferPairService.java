package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.application.usecase.account.AccountValidator;
import com.fyr.finapp.domain.api.transaction.DeleteTransferPairUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.model.transaction.exception.TransactionErrorCode;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import jakarta.transaction.Transactional;

public class DeleteTransferPairService implements DeleteTransferPairUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public DeleteTransferPairService(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            AccountValidator accountValidator) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    @Transactional
    public void delete(String transactionId, String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        var accId = AccountId.of(accountId);
        var txnId = TransactionId.of(transactionId);

        var account = accountValidator.getAccountAndValidateOwnership(accId, userId);

        var tx = transactionRepository.getTransactionByIdAndAccountId(txnId, accId)
                .orElseThrow(() -> new ValidationException(
                        "Transaction not found",
                        TransactionErrorCode.TRANSACTION_NOT_FOUND
                ));

        if (!tx.getType().isTransfer()) {
            throw new ValidationException("Transaction is not a transfer", TransactionErrorCode.TRANSACTION_NOT_FOUND);
        }

        // Determine the paired account ID for finding the other leg
        boolean isOut = tx.getToAccountId() != null;
        AccountId pairedAccountId = isOut ? tx.getToAccountId() : tx.getAccountId();

        // Reverse this leg
        if (isOut) {
            account.credit(tx.getAmount());
        } else {
            account.debit(tx.getAmount());
        }
        tx.softDelete();
        transactionRepository.save(tx);
        accountRepository.save(account);

        // Find and delete the paired leg
        transactionRepository.findPairedTransfer(txnId, pairedAccountId, tx.getOccurredOn(), tx.getAmount().amount(), userId)
                .ifPresent(paired -> {
                    var pairedAccount = accountRepository.findById(pairedAccountId)
                            .orElse(null);
                    if (pairedAccount != null) {
                        if (paired.getToAccountId() != null) {
                            pairedAccount.credit(paired.getAmount());
                        } else {
                            pairedAccount.debit(paired.getAmount());
                        }
                        accountRepository.save(pairedAccount);
                    }
                    paired.softDelete();
                    transactionRepository.save(paired);
                });
    }
}
