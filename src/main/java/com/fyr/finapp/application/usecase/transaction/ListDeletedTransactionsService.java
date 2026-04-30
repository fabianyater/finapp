package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.domain.api.transaction.ListDeletedTransactionsUseCase;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ListDeletedTransactionsService implements ListDeletedTransactionsUseCase {
    private static final Logger log = LoggerFactory.getLogger(ListDeletedTransactionsService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final ITransactionRepository transactionRepository;

    public ListDeletedTransactionsService(
            IAuthenticationRepository authenticationRepository,
            ITransactionRepository transactionRepository) {
        this.authenticationRepository = authenticationRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Result> execute(String accountId) {
        var userId = authenticationRepository.getCurrentUserId();
        log.debug("Listing deleted transactions accountId={} userId={}", accountId, userId.value());

        var accId = AccountId.of(accountId);
        List<Transaction> deleted = transactionRepository.findDeletedByAccountId(accId, userId);

        log.debug("Found {} deleted transactions for accountId={} userId={}", deleted.size(), accountId, userId.value());

        return deleted.stream().map(this::mapToResult).toList();
    }

    private Result mapToResult(Transaction t) {
        return new Result(
                t.getId().value().toString(),
                t.getType().name(),
                t.getAmount().amount(),
                t.getDescription(),
                t.getNote(),
                t.getOccurredOn().toString(),
                t.getDeletedAt().toString(),
                t.getCategoryId() != null ? t.getCategoryId().value().toString() : null,
                t.getAccountId().value().toString()
        );
    }
}
