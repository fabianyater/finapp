package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCode;

public record PersonName(String value) {
    public PersonName {
        if (value == null) throw new DomainException("name is required", ErrorCode.NAME_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new DomainException("name is blank", ErrorCode.NAME_BLANK);
        if (v.length() > 80)
            throw new DomainException("name too long, max 80 characters", ErrorCode.NAME_TOO_LONG);
        value = v;
    }
}
