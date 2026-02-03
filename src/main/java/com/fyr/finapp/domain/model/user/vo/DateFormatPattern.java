package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;
//TODO: Add unit tests for all VOs classes

public record DateFormatPattern(String value) {
    public DateFormatPattern {
        if (value == null)
            throw new ValidationException("dateFormat is required", UserErrorCode.DATE_FORMAT_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new ValidationException("dateFormat is blank", UserErrorCode.DATE_FORMAT_BLANK);
        if (v.length() > 40)
            throw new ValidationException("dateFormat too long", UserErrorCode.DATE_FORMAT_TOO_LONG);
        value = v;
    }
}
