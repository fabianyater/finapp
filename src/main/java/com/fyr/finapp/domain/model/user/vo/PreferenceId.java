package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.user.exception.UserErrorCode;

import java.util.UUID;

public record PreferenceId(UUID value) {
    public PreferenceId {
        if (value == null) throw new ValidationException("userId is required", UserErrorCode.PREFERENCE_ID_REQUIRED);
    }
}
