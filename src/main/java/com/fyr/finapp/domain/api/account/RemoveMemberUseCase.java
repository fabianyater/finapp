package com.fyr.finapp.domain.api.account;

public interface RemoveMemberUseCase {
    void remove(String accountId, String memberUserId);
}
