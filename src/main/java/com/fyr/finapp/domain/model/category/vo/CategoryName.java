package com.fyr.finapp.domain.model.category.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;

public record CategoryName(String value) {
    private static final int MAX_LENGTH = 255;

    public CategoryName {
        if (value == null) {
            throw new ValidationException("Category name cannot be null", CategoryErrorCode.NAME_REQUIRED);
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Category name cannot be blank", CategoryErrorCode.NAME_REQUIRED);
        }

        if (trimmed.length() > MAX_LENGTH) {
            throw new ValidationException("Category name cannot exceed " + MAX_LENGTH + " characters", CategoryErrorCode.NAME_TOO_LONG);
        }

        value = trimmed;
    }

    public static CategoryName of(String name) {
        return new CategoryName(name);
    }
}
