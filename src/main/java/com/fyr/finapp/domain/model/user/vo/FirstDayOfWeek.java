package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

public record FirstDayOfWeek(short value) {
    public FirstDayOfWeek {
        if (value < 1 || value > 7) {
            throw new DomainException("firstDayOfWeek must be 1..7", DomainException.ErrorCode.FIRST_DAY_OF_WEEK_INVALID);
        }
    }
}
