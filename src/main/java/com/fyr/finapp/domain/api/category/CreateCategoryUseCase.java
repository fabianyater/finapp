package com.fyr.finapp.domain.api.category;

public interface CreateCategoryUseCase {
    CategoryResult create(Command command);

    record CategoryResult(String id) {
    }

    record Command(
            String name,
            String icon,
            String color,
            String type) {
    }
}
