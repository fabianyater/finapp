package com.fyr.finapp.domain.api.transaction;

import com.fyr.finapp.domain.shared.pagination.PageRequest;
import com.fyr.finapp.domain.shared.pagination.PagedResult;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface ListTransactionUseCase {
    PagedResult<TransactionResult> list(Query query);

    record Query(
            PageRequest pageRequest,
            Set<String> accountIds,
            Set<String> categoryIds,
            Set<String> types,
            String search,
            Instant dateFrom,
            Instant dateTo,
            Set<String> tags) {
        private static final Set<String> VALID_SORT_FIELDS =
                Set.of("occurredOn", "amount", "createdAt", "description");

        public Query {
            accountIds = accountIds == null ? Set.of() : accountIds;
            categoryIds = categoryIds == null ? Set.of() : categoryIds;
            types = types == null ? Set.of() : types;
            tags = tags == null ? Set.of() : tags;

            pageRequest = pageRequest.withValidatedSortBy("occurredOn", VALID_SORT_FIELDS);
        }
    }

    record TransactionResult(
            String id,
            String type,
            long amount,
            String description,
            String note,
            String occurredOn,
            String accountId,
            String categoryId,
            String categoryName,
            String categoryColor,
            String categoryIcon,
            String toAccountId,
            List<String> tags,
            String createdBy) {
    }
}
