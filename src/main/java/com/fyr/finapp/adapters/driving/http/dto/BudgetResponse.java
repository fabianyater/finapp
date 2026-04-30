package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.domain.api.budget.ListBudgetsUseCase;

public record BudgetResponse(
        String id,
        String categoryId,
        String categoryName,
        String categoryColor,
        String categoryIcon,
        long limitAmount,
        long spentAmount
) {
    public static BudgetResponse from(ListBudgetsUseCase.BudgetResult r) {
        return new BudgetResponse(r.id(), r.categoryId(), r.categoryName(),
                r.categoryColor(), r.categoryIcon(), r.limitAmount(), r.spentAmount());
    }
}
