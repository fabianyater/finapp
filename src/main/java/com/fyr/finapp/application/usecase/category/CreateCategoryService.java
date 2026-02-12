package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.CreateCategoryUseCase;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Color;
import com.fyr.finapp.domain.shared.vo.Icon;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;

public class CreateCategoryService implements CreateCategoryUseCase {
    private final ICategoryRepository categoryRepository;
    private final IAuthenticationRepository authenticationRepository;

    public CreateCategoryService(ICategoryRepository categoryRepository,
                                 IAuthenticationRepository authenticationRepository) {
        this.categoryRepository = categoryRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    @Transactional
    public CategoryResult create(Command command) {
        var userId = authenticationRepository.getCurrentUserId();
        var category = createCategory(command, userId);

        if (categoryRepository.existsByUserIdAndTypeAndName(userId, category.getType(), category.getName())) {
            throw new ValidationException(
                    "Ya existe una categoría de tipo " + category.getType() + " con el nombre '" + category.getName().value() + "'",
                    CategoryErrorCode.NAME_ALREADY_EXISTS
            );
        }

        categoryRepository.save(category);

        return new CategoryResult(category.getId().value().toString());

    }

    private @NonNull Category createCategory(Command command, UserId userId) {
        CategoryName categoryName = CategoryName.of(command.name());
        TransactionType type = TransactionType.fromString(command.type());
        Color color = Color.of(command.color());
        Icon icon = Icon.of(command.icon());

        return Category.create(userId, categoryName, type, color, icon);
    }
}
