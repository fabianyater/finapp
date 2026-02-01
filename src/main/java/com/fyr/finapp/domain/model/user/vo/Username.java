package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Username(String value) {
    private static final Pattern P = Pattern.compile("^[a-zA-Z0-9._-]{3,30}$");

    public Username {
        if (value == null) throw new DomainException("username is required", DomainException.ErrorCode.USERNAME_REQUIRED);
        String v = value.trim();
        if (!P.matcher(v).matches()) throw new DomainException("invalid username", DomainException.ErrorCode.USERNAME_INVALID);
        value = v;
    }
}
