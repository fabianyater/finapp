package com.fyr.finapp.domain.spi.transaction;

import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.pagination.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ITransactionRepository {
    void save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    PagedTransactions findByUserId(UserId userId, TransactionFilters filters);

    Optional<Transaction> getTransactionByIdAndAccountId(TransactionId transactionId, AccountId id);

    record TransactionFilters(
            PageRequest pageRequest,
            Set<String> accountIds,
            Set<String> categoryIds,
            Set<String> types,
            String search,
            Instant dateFrom,
            Instant dateTo
    ) {
        public TransactionFilters {
            accountIds = accountIds == null ? Set.of() : accountIds;
            categoryIds = categoryIds == null ? Set.of() : categoryIds;
            types = types == null ? Set.of() : types;
        }
    }

    record PagedTransactions(
            List<Transaction> transactions,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
    ) {
    }
}
