package com.fyr.finapp.domain.spi.account;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAccountInvitationRepository {

    record PendingInvitation(UUID id, UUID accountId, UUID inviterId, UUID inviteeId, String status) {}

    record InvitationView(
            UUID id,
            UUID accountId,
            String accountName,
            String inviterName,
            String inviterEmail,
            Instant createdAt
    ) {}

    UUID save(UUID accountId, UUID inviterId, UUID inviteeId);

    List<InvitationView> findPending(UUID inviteeId);

    Optional<PendingInvitation> findById(UUID id);

    void accept(UUID id);

    void decline(UUID id);

    boolean hasPendingInvite(UUID accountId, UUID inviteeId);
}
