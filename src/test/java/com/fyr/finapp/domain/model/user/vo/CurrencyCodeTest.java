package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CurrencyCodeTest {

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        assertThrows(DomainException.class, () -> new CurrencyCode(null));
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsInvalid() {
        assertThrows(DomainException.class, () -> new CurrencyCode("INVALID"));
    }

    @Test
    void shouldAcceptValidCurrencyCodes() {
        assertDoesNotThrow(() -> new CurrencyCode("USD"));
        assertDoesNotThrow(() -> new CurrencyCode("EUR"));
        assertDoesNotThrow(() -> new CurrencyCode("GBP"));
    }

    @Test
    void shouldTrimAndConvertToUppercase() {
        CurrencyCode currencyCode = new CurrencyCode("  usd  ");
        assertEquals("USD", currencyCode.value());

        CurrencyCode lowerCaseCode = new CurrencyCode("eur");
        assertEquals("EUR", lowerCaseCode.value());
    }
}
