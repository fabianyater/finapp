package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
//TODO: Add unit tests for all VOs classes

public record DateFormatPattern(String value) {
    public DateFormatPattern {
        if (value == null) throw new DomainException("dateFormat is required", DomainException.ErrorCode.DATE_FORMAT_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new DomainException("dateFormat is blank", DomainException.ErrorCode.DATE_FORMAT_BLANK);
        if (v.length() > 40) throw new DomainException("dateFormat too long", DomainException.ErrorCode.DATE_FORMAT_TOO_LONG);
        value = v;
    }
}
