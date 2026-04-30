package com.fyr.finapp.domain.spi.llm;

import java.util.List;

public interface ILlmRepository {
    ParsedTransaction parseTransaction(
        String text,
        String today,
        List<CategoryContext> categories,
        List<AccountContext> accounts
    );

    record CategoryContext(String id, String name, String type, String icon) {}
    record AccountContext(String id, String name) {}
    record NewCategory(String name, String icon, String color) {}
    record ParsedTransaction(
        String type,
        long amount,
        String description,
        String note,
        String occurredOn,
        String categoryId,
        NewCategory newCategory
    ) {}
}
