package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.ListCategoriesUseCase;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;

import java.util.List;

public class ListAccountService implements ListCategoriesUseCase {
    private final ICategoryRepository categoryRepository;
    private final IAuthenticationRepository authenticationRepository;

    public ListAccountService(ICategoryRepository categoryRepository,
                              IAuthenticationRepository authenticationRepository) {
        this.categoryRepository = categoryRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public List<CategoryResult> execute() {
        var userId = authenticationRepository.getCurrentUserId().value().toString();
        return categoryRepository.findAllByUserId(UserId.of(userId))
                .stream()
                .map(this::toResult)
                .toList();
    }

    @Override
    public List<CategoryResult> executeDeleted() {
        var userId = authenticationRepository.getCurrentUserId().value().toString();
        return categoryRepository.findAllDeletedByUserId(UserId.of(userId))
                .stream()
                .map(this::toResult)
                .toList();
    }

    private CategoryResult toResult(com.fyr.finapp.domain.model.category.Category category) {
        return new CategoryResult(
                category.getId().value().toString(),
                category.getName().value(),
                category.getType().name(),
                category.getColor().value(),
                category.getIcon().name(),
                category.getCreatedAt().toString()
        );
    }
}
