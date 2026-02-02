package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCode;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record PlainPassword(String value) {
    public PlainPassword {
        if (value == null)
            throw new DomainException("password is required", ErrorCode.PASSWORD_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new DomainException("password is blank", ErrorCode.PASSWORD_BLANK);


        if (v.length() < UserConstraints.PASSWORD_MIN_LENGTH)
            throw new DomainException("password too short", ErrorCode.PASSWORD_TOO_SHORT);
        if (v.length() > UserConstraints.PASSWORD_MAX_LENGTH)
            throw new DomainException("password too long", ErrorCode.PASSWORD_TOO_LONG);

        value = v;
    }
}
