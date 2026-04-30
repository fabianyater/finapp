package com.fyr.finapp.domain.shared.vo;

import com.fyr.finapp.domain.exception.ValidationException;

import java.time.LocalDate;

public enum RecurringFrequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    YEARLY;

    public LocalDate nextDate(LocalDate from) {
        return switch (this) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case BIWEEKLY -> from.plusWeeks(2);
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }

    public static RecurringFrequency fromString(String value) {
        try {
            return RecurringFrequency.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid frequency: " + value + ". Must be DAILY, WEEKLY, BIWEEKLY, MONTHLY or YEARLY",
                    null
            );
        }
    }
}
