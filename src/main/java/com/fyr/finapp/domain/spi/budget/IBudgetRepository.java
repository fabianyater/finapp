package com.fyr.finapp.domain.spi.budget;

import com.fyr.finapp.domain.model.budget.Budget;
import com.fyr.finapp.domain.model.budget.BudgetId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IBudgetRepository {
    void save(Budget budget);
    Optional<Budget> findById(BudgetId id);
    Optional<Budget> findByCategoryIdAndUserId(CategoryId categoryId, UserId userId);
    List<BudgetWithSpent> findAllWithSpent(UserId userId, Instant periodStart, Instant periodEnd);
    void delete(BudgetId id);

    record BudgetWithSpent(
            BudgetId id,
            CategoryId categoryId,
            String categoryName,
            String categoryColor,
            String categoryIcon,
            long limitAmount,
            long spentAmount
    ) {}
}
