package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern P = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null) throw new DomainException("email is required", DomainException.ErrorCode.EMAIL_REQUIRED);
        String v = value.trim().toLowerCase();
        if (v.isBlank()) throw new DomainException("email is blank", DomainException.ErrorCode.EMAIL_BLANK);
        if (v.length() > 254) throw new DomainException("email too long", DomainException.ErrorCode.EMAIL_TOO_LONG);
        if (!P.matcher(v).matches()) throw new DomainException("invalid email", DomainException.ErrorCode.EMAIL_INVALID);
        value = v;
    }
}
