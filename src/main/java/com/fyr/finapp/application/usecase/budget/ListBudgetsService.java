package com.fyr.finapp.application.usecase.budget;

import com.fyr.finapp.domain.api.budget.ListBudgetsUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.budget.IBudgetRepository;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

public class ListBudgetsService implements ListBudgetsUseCase {
    private final IAuthenticationRepository authenticationRepository;
    private final IBudgetRepository budgetRepository;

    public ListBudgetsService(IAuthenticationRepository authenticationRepository,
                              IBudgetRepository budgetRepository) {
        this.authenticationRepository = authenticationRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public List<BudgetResult> list() {
        var userId = authenticationRepository.getCurrentUserId();
        var month = YearMonth.now();
        var periodStart = month.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        var periodEnd = month.plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        return budgetRepository.findAllWithSpent(userId, periodStart, periodEnd)
                .stream()
                .map(b -> new BudgetResult(
                        b.id().value().toString(),
                        b.categoryId().value().toString(),
                        b.categoryName(),
                        b.categoryColor(),
                        b.categoryIcon(),
                        b.limitAmount(),
                        b.spentAmount()
                ))
                .toList();
    }
}
