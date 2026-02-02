package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.exception.ErrorCode;

import java.util.Locale;

public record LocaleTag(String value) {
    public LocaleTag {
        if (value == null) throw new DomainException("locale is required", ErrorCode.LOCALE_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new DomainException("locale is blank", ErrorCode.LOCALE_BLANK);

        try {
            Locale.forLanguageTag(v);
        } catch (Exception e) {
            throw new DomainException("invalid locale", ErrorCode.LOCALE_INVALID);
        }
        value = v;
    }
}
