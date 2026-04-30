package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.UserConstraints;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

public record Username(String value) {
    public Username {
        if (value == null)
            throw new ValidationException("username is required", UserErrorCode.USERNAME_REQUIRED);
        String v = value.trim();
        if (!UserConstraints.USERNAME_PATTERN.matcher(v).matches())
            throw new ValidationException("invalid username", UserErrorCode.USERNAME_INVALID);
        value = v;
    }
}
