package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record Email(String value) {
    public Email {
        if (value == null) throw new DomainException("email is required", DomainException.ErrorCode.EMAIL_REQUIRED);
        String v = value.trim().toLowerCase();
        if (v.isBlank()) throw new DomainException("email is blank", DomainException.ErrorCode.EMAIL_BLANK);
        if (v.length() > UserConstraints.EMAIL_MAX_LENGTH)
            throw new DomainException("email too long", DomainException.ErrorCode.EMAIL_TOO_LONG);
        if (!UserConstraints.EMAIL_PATTERN.matcher(v).matches())
            throw new DomainException("invalid email", DomainException.ErrorCode.EMAIL_INVALID);
        value = v;
    }
}
