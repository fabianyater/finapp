package com.fyr.finapp.domain.api.category;

public interface UpdateCategoryUseCase {
    void update(Command command);

    record Command(
            String categoryId,
            String name,
            String type,
            String color,
            String icon
    ) {
    }
}
