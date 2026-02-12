package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.DeleteCategoryUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;

public class DeleteCategoryService implements DeleteCategoryUseCase {
    private final ICategoryRepository categoryRepository;
    private final IAuthenticationRepository authenticationRepository;

    public DeleteCategoryService(ICategoryRepository categoryRepository,
                                 IAuthenticationRepository authenticationRepository) {
        this.categoryRepository = categoryRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public void delete(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var categoryId = CategoryId.of(command.categoryId());
        var category = getCategoryAndValidateOwnership(categoryId, userId);

        boolean wasDeleted = category.markAsDeleted();

        if (wasDeleted) {
            categoryRepository.save(category);
        }
    }

    private Category getCategoryAndValidateOwnership(CategoryId categoryId, UserId userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(
                        "Category not found or unauthorized",
                        CategoryErrorCode.CATEGORY_NOT_FOUND
                ));

        if (!category.getUserId().equals(userId)) {
            throw new ForbiddenException(
                    "Category not found or unauthorized",
                    CategoryErrorCode.ACCESS_DENIED
            );
        }

        return category;
    }
}
