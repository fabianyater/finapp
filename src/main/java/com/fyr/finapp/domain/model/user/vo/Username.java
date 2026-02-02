package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCode;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record Username(String value) {
    public Username {
        if (value == null)
            throw new DomainException("username is required", ErrorCode.USERNAME_REQUIRED);
        String v = value.trim();
        if (!UserConstraints.USERNAME_PATTERN.matcher(v).matches())
            throw new DomainException("invalid username", ErrorCode.USERNAME_INVALID);
        value = v;
    }
}
