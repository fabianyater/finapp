package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.GetCategoryTemplatesUseCase;
import com.fyr.finapp.domain.model.category.CategoryTemplate;

import java.util.List;

public class GetCategoryTemplatesService implements GetCategoryTemplatesUseCase {

    @Override
    public List<TemplateResult> execute() {
        return CategoryTemplate.all().stream()
                .map(t -> new TemplateResult(t.key(), t.name(), t.type(), t.color(), t.icon()))
                .toList();
    }
}
