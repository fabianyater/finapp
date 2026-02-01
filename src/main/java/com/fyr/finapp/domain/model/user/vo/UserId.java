package com.fyr.finapp.domain.model.user.vo;

import com.fyr.finapp.domain.exception.DomainException;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) throw new DomainException("userId is required", DomainException.ErrorCode.USER_ID_REQUIRED);
    }
}
