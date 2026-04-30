package com.fyr.finapp.domain.api.account;

public interface InviteMemberUseCase {
    void invite(String accountId, String email);
}
