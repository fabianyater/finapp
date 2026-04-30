package com.fyr.finapp.domain.api.category;

public interface DeleteCategoryUseCase {
    void delete(Command command);

    record Command(
            String categoryId
    ) {
    }
}
