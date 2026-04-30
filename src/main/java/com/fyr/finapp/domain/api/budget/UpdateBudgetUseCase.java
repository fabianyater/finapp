package com.fyr.finapp.domain.api.budget;

public interface UpdateBudgetUseCase {
    void update(Command command);

    record Command(String budgetId, long limitAmount) {}
}
