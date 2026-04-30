package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.UserConstraints;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

public record FirstDayOfWeek(short value) {
    public FirstDayOfWeek {
        if (value < UserConstraints.FIRST_DAY_OF_WEEK_MIN || value > UserConstraints.FIRST_DAY_OF_WEEK_MAX) {
            throw new ValidationException("firstDayOfWeek must be 1..7", UserErrorCode.FIRST_DAY_OF_WEEK_INVALID);
        }
    }
}
