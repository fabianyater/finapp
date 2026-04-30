package com.fyr.finapp.adapters.driven.persistence.jpa.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class AccountMemberId implements Serializable {
    private UUID accountId;
    private UUID userId;

    public AccountMemberId() {}

    public AccountMemberId(UUID accountId, UUID userId) {
        this.accountId = accountId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountMemberId that)) return false;
        return Objects.equals(accountId, that.accountId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId);
    }
}
