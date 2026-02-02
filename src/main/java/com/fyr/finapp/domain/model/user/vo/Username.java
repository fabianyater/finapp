package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record Username(String value) {
    public Username {
        if (value == null)
            throw new DomainException("username is required", DomainException.ErrorCode.USERNAME_REQUIRED);
        String v = value.trim();
        if (!UserConstraints.USERNAME_PATTERN.matcher(v).matches())
            throw new DomainException("invalid username", DomainException.ErrorCode.USERNAME_INVALID);
        value = v;
    }
}
