package com.fyr.finapp.domain.api.account;

import java.time.Instant;
import java.util.List;

public interface ListPendingInvitationsUseCase {

    record InvitationResult(
            String id,
            String accountId,
            String accountName,
            String inviterName,
            String inviterEmail,
            Instant createdAt
    ) {}

    List<InvitationResult> list();
}
