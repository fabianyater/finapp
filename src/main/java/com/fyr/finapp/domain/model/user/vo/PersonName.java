package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

public record PersonName(String value) {
    public PersonName {
        if (value == null) throw new ValidationException("name is required", UserErrorCode.NAME_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new ValidationException("name is blank", UserErrorCode.NAME_BLANK);
        if (v.length() > 80)
            throw new ValidationException("name too long, max 80 characters", UserErrorCode.NAME_TOO_LONG);
        value = v;
    }
}
