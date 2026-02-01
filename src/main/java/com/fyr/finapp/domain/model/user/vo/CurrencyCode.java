package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

import java.util.Currency;

public record CurrencyCode(String value) {
    public CurrencyCode {
        if (value == null) throw new DomainException("currency is required", DomainException.ErrorCode.CURRENCY_REQUIRED);
        String v = value.trim().toUpperCase();
        try {
            Currency.getInstance(v);
        } catch (Exception e) {
            throw new DomainException("invalid currency", DomainException.ErrorCode.CURRENCY_INVALID);
        }
        value = v;
    }
}
