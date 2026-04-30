package com.fyr.finapp.domain.api.category;

public interface RestoreCategoryUseCase {
    void restore(Command command);

    record Command(
            String categoryId
    ) {
    }
}
