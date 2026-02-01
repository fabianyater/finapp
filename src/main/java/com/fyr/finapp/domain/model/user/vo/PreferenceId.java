package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

import java.util.UUID;

public record PreferenceId(UUID value) {
    public PreferenceId {
        if (value == null) throw new DomainException("userId is required", DomainException.ErrorCode.PREFERENCE_ID_REQUIRED);
    }
}
