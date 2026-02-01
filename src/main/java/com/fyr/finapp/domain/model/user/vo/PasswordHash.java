package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null) throw new DomainException("passwordHash is required", DomainException.ErrorCode.PASSWORD_HASH_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new DomainException("passwordHash is blank", DomainException.ErrorCode.PASSWORD_HASH_BLANK);
        if (v.length() < 20) throw new DomainException("passwordHash too short", DomainException.ErrorCode.PASSWORD_HASH_TOO_SHORT);
        value = v;
    }
}
