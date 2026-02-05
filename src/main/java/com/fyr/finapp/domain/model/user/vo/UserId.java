package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) throw new ValidationException("userId is required", UserErrorCode.ID_REQUIRED);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(String id) {
        try {
            return new UserId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid User ID format: " + id, UserErrorCode.ID_INVALID);
        }
    }
}
