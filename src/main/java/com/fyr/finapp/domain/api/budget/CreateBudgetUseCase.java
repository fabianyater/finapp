package com.fyr.finapp.domain.api.budget;

public interface CreateBudgetUseCase {
    Result create(Command command);

    record Command(String categoryId, long limitAmount) {}
    record Result(String id) {}
}
