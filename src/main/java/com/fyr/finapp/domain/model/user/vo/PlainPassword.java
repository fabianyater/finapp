package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record PlainPassword(String value) {
    public PlainPassword {
        if (value == null)
            throw new DomainException("password is required", DomainException.ErrorCode.PASSWORD_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new DomainException("password is blank", DomainException.ErrorCode.PASSWORD_BLANK);


        if (v.length() < UserConstraints.PASSWORD_MIN_LENGTH)
            throw new DomainException("password too short", DomainException.ErrorCode.PASSWORD_TOO_SHORT);
        if (v.length() > UserConstraints.PASSWORD_MAX_LENGTH)
            throw new DomainException("password too long", DomainException.ErrorCode.PASSWORD_TOO_LONG);

        value = v;
    }
}
