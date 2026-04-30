package com.fyr.finapp.domain.api.category;

import java.util.List;

public interface ListCategoriesUseCase {
    List<CategoryResult> execute();
    List<CategoryResult> executeDeleted();

    record CategoryResult(
            String id,
            String name,
            String type,
            String color,
            String icon,
            String createdAt
    ) {
    }
}
