package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.domain.api.transaction.ListTransactionUseCase;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.shared.pagination.PagedResult;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListTransactionService implements ListTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(ListTransactionService.class);

    private final ITransactionRepository transactionRepository;
    private final IAuthenticationRepository authenticationRepository;

    public ListTransactionService(ITransactionRepository transactionRepository, IAuthenticationRepository authenticationRepository) {
        this.transactionRepository = transactionRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public PagedResult<TransactionResult> list(Query query) {
        var userId = authenticationRepository.getCurrentUserId();
        log.debug("Listing transactions userId={} page={} size={}",
                userId.value(), query.pageRequest().page(), query.pageRequest().size());

        var filters = mapToFilters(query);
        var paged = transactionRepository.findByUserId(userId, filters);

        log.debug("Found {} transactions for userId={}", paged.totalElements(), userId.value());

        return new PagedResult<>(
                paged.transactions().stream().map(this::mapToResult).toList(),
                query.pageRequest().page(),
                query.pageRequest().size(),
                paged.totalElements(),
                paged.totalPages(),
                paged.hasNext(),
                paged.hasPrevious()
        );
    }

    private ITransactionRepository.TransactionFilters mapToFilters(Query query) {
        return new ITransactionRepository.TransactionFilters(
                query.pageRequest(),
                query.accountIds(),
                query.categoryIds(),
                query.types(),
                query.search(),
                query.dateFrom(),
                query.dateTo()
        );
    }

    private TransactionResult mapToResult(Transaction transaction) {
        return new TransactionResult(
                transaction.getId().value().toString(),
                transaction.getType().name(),
                transaction.getAmount().amount(),
                transaction.getDescription(),
                transaction.getNote(),
                transaction.getOccurredOn().toString(),
                transaction.getAccountId().value().toString(),
                transaction.getCategoryId() != null ? transaction.getCategoryId().value().toString() : null
        );
    }
}
