package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.UserConstraints;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

public record Email(String value) {
    public Email {
        if (value == null) throw new ValidationException("email is required", UserErrorCode.EMAIL_REQUIRED);
        String v = value.trim().toLowerCase();
        if (v.isBlank()) throw new ValidationException("email is blank", UserErrorCode.EMAIL_BLANK);
        if (v.length() > UserConstraints.EMAIL_MAX_LENGTH)
            throw new ValidationException("email too long", UserErrorCode.EMAIL_TOO_LONG);
        if (!UserConstraints.EMAIL_PATTERN.matcher(v).matches())
            throw new ValidationException("invalid email", UserErrorCode.EMAIL_INVALID);
        value = v;
    }
}
