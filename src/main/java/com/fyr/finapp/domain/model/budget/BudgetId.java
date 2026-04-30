package com.fyr.finapp.domain.model.budget;

import java.util.UUID;

public record BudgetId(UUID value) {
    public BudgetId {
        if (value == null) throw new IllegalArgumentException("BudgetId cannot be null");
    }

    public static BudgetId generate() {
        return new BudgetId(UUID.randomUUID());
    }

    public static BudgetId of(String id) {
        return new BudgetId(UUID.fromString(id));
    }
}
