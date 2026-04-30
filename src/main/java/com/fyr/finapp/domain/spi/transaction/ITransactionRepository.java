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
            Instant dateTo,
            Set<String> tags
    ) {
        public TransactionFilters {
            accountIds = accountIds == null ? Set.of() : accountIds;
            categoryIds = categoryIds == null ? Set.of() : categoryIds;
            types = types == null ? Set.of() : types;
            tags = tags == null ? Set.of() : tags;
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

    record CategorySummaryEntry(
            String categoryId,
            String name,
            String color,
            String icon,
            long total
    ) {
    }

    List<CategorySummaryEntry> findCategorySummary(UserId userId, String accountId, String type, Instant dateFrom, Instant dateTo);

    List<Transaction> findDeletedByAccountId(AccountId accountId, UserId userId);

    Optional<Transaction> findDeletedByIdAndAccountId(TransactionId id, AccountId accountId);

    Optional<Transaction> findPairedTransfer(TransactionId excludeId, AccountId pairedAccountId, Instant occurredOn, Long amount, UserId userId);

    List<Transaction> findAllByUserId(UserId userId, TransactionFilters filters);

    List<String> findAllTagsByUserId(UserId userId);

    void renameTag(UserId userId, String oldTag, String newTag);

    void deleteTag(UserId userId, String tag);
}
