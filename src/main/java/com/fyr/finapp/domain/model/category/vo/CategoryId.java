package com.fyr.finapp.domain.model.category.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.category.exception.CategoryErrorCode;

import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId {
        if (value == null) {
            throw new ValidationException(
                    "Category ID cannot be null",
                    CategoryErrorCode.ID_REQUIRED
            );
        }
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId of(String id) {
        try {
            return new CategoryId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid Category ID format: " + id, CategoryErrorCode.ID_INVALID);
        }
    }

}