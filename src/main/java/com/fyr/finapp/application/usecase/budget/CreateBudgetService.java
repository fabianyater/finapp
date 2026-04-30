package com.fyr.finapp.application.usecase.budget;

import com.fyr.finapp.domain.api.budget.CreateBudgetUseCase;
import com.fyr.finapp.domain.exception.ConflictException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.model.budget.Budget;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.budget.IBudgetRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import jakarta.transaction.Transactional;

public class CreateBudgetService implements CreateBudgetUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IBudgetRepository budgetRepository;
    private final ICategoryRepository categoryRepository;

    public CreateBudgetService(IAuthenticationRepository authenticationRepository,
                               IBudgetRepository budgetRepository,
                               ICategoryRepository categoryRepository) {
        this.authenticationRepository = authenticationRepository;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Result create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var categoryId = CategoryId.of(command.categoryId());

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found", null));

        if (!category.belongsToUser(userId)) {
            throw new NotFoundException("Category not found", null);
        }

        budgetRepository.findByCategoryIdAndUserId(categoryId, userId).ifPresent(b -> {
            throw new ConflictException("A budget already exists for this category", null);
        });

        var budget = Budget.create(userId, categoryId, command.limitAmount());
        budgetRepository.save(budget);

        return new Result(budget.getId().value().toString());
    }
}
