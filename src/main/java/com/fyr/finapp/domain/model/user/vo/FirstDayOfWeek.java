package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;
import com.fyr.finapp.domain.model.user.UserConstraints;

public record FirstDayOfWeek(short value) {
    public FirstDayOfWeek {
        if (value < UserConstraints.FIRST_DAY_OF_WEEK_MIN || value > UserConstraints.FIRST_DAY_OF_WEEK_MAX) {
            throw new DomainException("firstDayOfWeek must be 1..7", DomainException.ErrorCode.FIRST_DAY_OF_WEEK_INVALID);
        }
    }
}
