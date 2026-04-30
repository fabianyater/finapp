package com.fyr.finapp.domain.api.category;

import java.util.List;

public interface GetCategoryTemplatesUseCase {
    List<TemplateResult> execute();

    record TemplateResult(String key, String name, String type, String color, String icon) {
    }
}
