package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

import java.time.ZoneId;

public record TimezoneId(String value) {
    public TimezoneId {
        if (value == null) throw new ValidationException("timezone is required", UserErrorCode.TIMEZONE_REQUIRED);
        String v = value.trim();
        try {
            ZoneId.of(v);
        } catch (Exception e) {
            throw new ValidationException("invalid timezone", UserErrorCode.TIMEZONE_INVALID);
        }
        value = v;
    }
}
