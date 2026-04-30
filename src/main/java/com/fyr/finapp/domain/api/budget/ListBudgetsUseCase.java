package com.fyr.finapp.domain.api.budget;

import java.util.List;

public interface ListBudgetsUseCase {
    List<BudgetResult> list();

    record BudgetResult(
            String id,
            String categoryId,
            String categoryName,
            String categoryColor,
            String categoryIcon,
            long limitAmount,
            long spentAmount
    ) {}
}
