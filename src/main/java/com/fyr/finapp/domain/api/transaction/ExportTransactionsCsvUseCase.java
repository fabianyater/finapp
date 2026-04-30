package com.fyr.finapp.domain.api.transaction;

import java.time.Instant;
import java.util.Set;

public interface ExportTransactionsCsvUseCase {
    String export(Query query);

    record Query(
            Set<String> accountIds,
            Set<String> categoryIds,
            Set<String> types,
            String search,
            Instant dateFrom,
            Instant dateTo) {
        public Query {
            accountIds = accountIds == null ? Set.of() : accountIds;
            categoryIds = categoryIds == null ? Set.of() : categoryIds;
            types = types == null ? Set.of() : types;
        }
    }
}
