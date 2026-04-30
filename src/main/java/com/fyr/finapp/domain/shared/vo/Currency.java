package com.fyr.finapp.domain.shared.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.shared.exception.SharedErrorCode;

import static com.fyr.finapp.domain.shared.constraints.CommonConstraints.CURRENCY_PATTERN;

public record Currency(String code) {
    public Currency {
        if (code == null || !CURRENCY_PATTERN.matcher(code).matches()) {
            throw new ValidationException(
                    "Invalid currency code: " + code + ". Must be 3 uppercase letters (e.g., COP, USD)",
                    SharedErrorCode.INVALID_CURRENCY_CODE
            );
        }
    }

    public static Currency of(String code) {
        return new Currency(code);
    }

    public static Currency COP = new Currency("COP");
    public static Currency USD = new Currency("USD");
    public static Currency EUR = new Currency("EUR");
}
