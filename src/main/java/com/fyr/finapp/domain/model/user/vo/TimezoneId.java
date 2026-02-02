package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCode;

import java.time.ZoneId;

public record TimezoneId(String value) {
    public TimezoneId {
        if (value == null) throw new DomainException("timezone is required", ErrorCode.TIMEZONE_REQUIRED);
        String v = value.trim();
        try {
            ZoneId.of(v);
        } catch (Exception e) {
            throw new DomainException("invalid timezone", ErrorCode.TIMEZONE_INVALID);
        }
        value = v;
    }
}
