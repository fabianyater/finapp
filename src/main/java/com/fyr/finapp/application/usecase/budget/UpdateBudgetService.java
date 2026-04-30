package com.fyr.finapp.application.usecase.budget;

import com.fyr.finapp.domain.api.budget.UpdateBudgetUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.model.budget.BudgetId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.budget.IBudgetRepository;
import jakarta.transaction.Transactional;

public class UpdateBudgetService implements UpdateBudgetUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IBudgetRepository budgetRepository;

    public UpdateBudgetService(IAuthenticationRepository authenticationRepository,
                               IBudgetRepository budgetRepository) {
        this.authenticationRepository = authenticationRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    @Transactional
    public void update(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var budget = budgetRepository.findById(BudgetId.of(command.budgetId()))
                .orElseThrow(() -> new NotFoundException("Budget not found", null));

        if (!budget.belongsToUser(userId)) throw new ForbiddenException("Access denied", null);

        budget.updateLimit(command.limitAmount());
        budgetRepository.save(budget);
    }
}
