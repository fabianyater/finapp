package com.fyr.finapp.application.usecase.budget;

import com.fyr.finapp.domain.api.budget.DeleteBudgetUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.model.budget.BudgetId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.budget.IBudgetRepository;
import jakarta.transaction.Transactional;

public class DeleteBudgetService implements DeleteBudgetUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IBudgetRepository budgetRepository;

    public DeleteBudgetService(IAuthenticationRepository authenticationRepository,
                               IBudgetRepository budgetRepository) {
        this.authenticationRepository = authenticationRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    @Transactional
    public void delete(String budgetId) {
        var userId = authenticationRepository.getCurrentUserId();
        var budget = budgetRepository.findById(BudgetId.of(budgetId))
                .orElseThrow(() -> new NotFoundException("Budget not found", null));

        if (!budget.belongsToUser(userId)) throw new ForbiddenException("Access denied", null);

        budgetRepository.delete(budget.getId());
    }
}
