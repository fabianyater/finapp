package com.fyr.finapp.domain.api.category;

import java.util.List;

public interface GetCategorySummaryUseCase {
    List<SummaryResult> execute(Query query);

    record Query(String accountId, String type, java.time.Instant dateFrom, java.time.Instant dateTo) {
    }

    record SummaryResult(
            String categoryId,
            String name,
            String color,
            String icon,
            long total
    ) {
    }
}
