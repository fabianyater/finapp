package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.UserConstraints;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

public record PlainPassword(String value) {
    public PlainPassword {
        if (value == null)
            throw new ValidationException("password is required", UserErrorCode.PASSWORD_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new ValidationException("password is blank", UserErrorCode.PASSWORD_BLANK);


        if (v.length() < UserConstraints.PASSWORD_MIN_LENGTH)
            throw new ValidationException("password too short", UserErrorCode.PASSWORD_TOO_SHORT);
        if (v.length() > UserConstraints.PASSWORD_MAX_LENGTH)
            throw new ValidationException("password too long", UserErrorCode.PASSWORD_TOO_LONG);

        value = v;
    }
}
