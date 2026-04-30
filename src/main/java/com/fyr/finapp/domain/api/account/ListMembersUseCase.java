package com.fyr.finapp.domain.api.account;

import java.time.Instant;
import java.util.List;

public interface ListMembersUseCase {
    List<MemberResult> list(String accountId);

    record MemberResult(String userId, String email, String name, Instant joinedAt) {}
}
