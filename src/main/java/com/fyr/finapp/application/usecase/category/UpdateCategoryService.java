package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.UpdateCategoryUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.NotFoundException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Color;
import com.fyr.finapp.domain.shared.vo.Icon;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import jakarta.transaction.Transactional;

public class UpdateCategoryService implements UpdateCategoryUseCase {
    private final ICategoryRepository categoryRepository;
    private final IAuthenticationRepository authenticationRepository;

    public UpdateCategoryService(ICategoryRepository categoryRepository,
                                 IAuthenticationRepository authenticationRepository) {
        this.categoryRepository = categoryRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    @Transactional
    public void update(Command command) {
        var categoryId = CategoryId.of(command.categoryId());
        var userId = authenticationRepository.getCurrentUserId();

        Category existingCategory = getCategoryAndValidateOwnership(categoryId, userId);

        var newName = CategoryName.of(command.name());
        validateUniqueNameForUpdate(existingCategory, newName, userId);

        existingCategory.update(
                newName,
                TransactionType.fromString(command.type()),
                Color.of(command.color()),
                Icon.of(command.icon())
        );

        categoryRepository.save(existingCategory);
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

    private void validateUniqueNameForUpdate(Category existingCategory, CategoryName newName, UserId userId) {
        if (existingCategory.getName().equals(newName)) return;

        if (categoryRepository.existsByUserIdAndTypeAndName(userId, existingCategory.getType(), newName)) {
            throw new ValidationException(
                    "A category of type " + existingCategory.getType() + " with the name '" + newName.value() + "' already exists",
                    CategoryErrorCode.NAME_ALREADY_EXISTS
            );
        }
    }
}
