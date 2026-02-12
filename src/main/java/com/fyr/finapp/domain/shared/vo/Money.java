package com.fyr.finapp.domain.shared.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.shared.exception.SharedErrorCode;

import java.math.BigDecimal;

public record Money(Long amount, Currency currency) {
    public Money {
        if (amount == null) {
            throw new ValidationException("Amount cannot be null", SharedErrorCode.AMOUNT_REQUIRED);
        }

        if (currency == null) {
            throw new ValidationException("Currency cannot be null", SharedErrorCode.INVALID_CURRENCY_CODE);
        }
    }

    public static Money zero(Currency currency) {
        return new Money(0L, currency);
    }

    public static Money of(Long amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(Long amount, String currencyCode) {
        return new Money(amount, Currency.of(currencyCode));
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount + other.amount, this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount - other.amount, this.currency);
    }

    public boolean isPositive() {
        return amount > 0;
    }

    public boolean isNegative() {
        return amount < 0;
    }

    public boolean isZero() {
        return amount == 0;
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(amount).movePointLeft(2);
    }

    public static Money fromBigDecimal(BigDecimal amount, Currency currency) {
        long cents = amount.multiply(BigDecimal.valueOf(100)).longValue();
        return new Money(cents, currency);
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new ValidationException(
                    "Cannot operate with different currencies: " +
                            this.currency + " and " + other.currency,
                    SharedErrorCode.CURRENCY_MISMATCH
            );
        }
    }
}
