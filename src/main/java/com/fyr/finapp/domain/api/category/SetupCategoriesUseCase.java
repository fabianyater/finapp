package com.fyr.finapp.domain.api.category;

import java.util.List;

public interface SetupCategoriesUseCase {
    void setup(Command command);

    record Command(List<String> keys) {
    }
}
