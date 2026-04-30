package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.UserConstraints;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null)
            throw new ValidationException("passwordHash is required", UserErrorCode.PASSWORD_HASH_REQUIRED);
        String v = value.trim();
        if (v.isBlank())
            throw new ValidationException("passwordHash is blank", UserErrorCode.PASSWORD_HASH_BLANK);
        if (v.length() < UserConstraints.HASHED_PASSWORD_MIN_LENGTH)
            throw new ValidationException("passwordHash too short", UserErrorCode.PASSWORD_HASH_TOO_SHORT);
        value = v;
    }
}
