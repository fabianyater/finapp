package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null)
            throw new DomainException("passwordHash is required", DomainException.ErrorCode.PASSWORD_HASH_REQUIRED);
        String v = value.trim();
        if (v.isBlank())
            throw new DomainException("passwordHash is blank", DomainException.ErrorCode.PASSWORD_HASH_BLANK);
        if (v.length() < UserConstraints.HASHED_PASSWORD_MIN_LENGTH)
            throw new DomainException("passwordHash too short", DomainException.ErrorCode.PASSWORD_HASH_TOO_SHORT);
        value = v;
    }
}
