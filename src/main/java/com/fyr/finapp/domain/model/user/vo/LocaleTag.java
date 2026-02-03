package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

import java.util.Locale;

public record LocaleTag(String value) {
    public LocaleTag {
        if (value == null) throw new ValidationException("locale is required", UserErrorCode.LOCALE_REQUIRED);
        String v = value.trim();
        if (v.isBlank()) throw new ValidationException("locale is blank", UserErrorCode.LOCALE_BLANK);

        try {
            Locale.forLanguageTag(v);
        } catch (Exception e) {
            throw new ValidationException("invalid locale", UserErrorCode.LOCALE_INVALID);
        }
        value = v;
    }
}
