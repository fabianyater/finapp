package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.SetupCategoriesUseCase;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.CategoryTemplate;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetupCategoriesService implements SetupCategoriesUseCase {
    private final ICategoryRepository categoryRepository;
    private final IAuthenticationRepository authenticationRepository;

    public SetupCategoriesService(ICategoryRepository categoryRepository,
                                   IAuthenticationRepository authenticationRepository) {
        this.categoryRepository = categoryRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    @Transactional
    public void setup(Command command) {
        var userId = authenticationRepository.getCurrentUserId();

        Map<String, CategoryTemplate> templatesByKey = CategoryTemplate.all().stream()
                .collect(Collectors.toMap(CategoryTemplate::key, Function.identity()));

        List<Category> categories = command.keys().stream()
                .filter(templatesByKey::containsKey)
                .map(templatesByKey::get)
                .filter(t -> !categoryRepository.existsByUserIdAndTypeAndName(
                        userId,
                        TransactionType.fromString(t.type()),
                        CategoryName.of(t.name())))
                .map(t -> Category.createFromTemplate(userId, t))
                .toList();

        if (!categories.isEmpty()) {
            categoryRepository.saveAll(categories);
        }
    }
}
