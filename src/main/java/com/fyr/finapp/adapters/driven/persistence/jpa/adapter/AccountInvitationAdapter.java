package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountInvitationEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.AccountInvitationJpaRepository;
import com.fyr.finapp.domain.spi.account.IAccountInvitationRepository;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountInvitationAdapter implements IAccountInvitationRepository {

    private final AccountInvitationJpaRepository jpaRepository;

    @Override
    public UUID save(UUID accountId, UUID inviterId, UUID inviteeId) {
        var entity = new AccountInvitationEntity();
        entity.setAccountId(accountId);
        entity.setInviterId(inviterId);
        entity.setInviteeId(inviteeId);
        return jpaRepository.save(entity).getId();
    }

    @Override
    public List<InvitationView> findPending(UUID inviteeId) {
        return jpaRepository.findPendingByInviteeId(inviteeId).stream()
                .map(e -> new InvitationView(
                        e.getId(),
                        e.getAccountId(),
                        e.getAccount().getName(),
                        e.getInviter().getName() + " " + e.getInviter().getSurname(),
                        e.getInviter().getEmail(),
                        e.getCreatedAt().toInstant()
                ))
                .toList();
    }

    @Override
    public Optional<PendingInvitation> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> new PendingInvitation(
                        e.getId(), e.getAccountId(), e.getInviterId(), e.getInviteeId(), e.getStatus()
                ));
    }

    @Override
    public void accept(UUID id) {
        jpaRepository.findById(id).ifPresent(e -> {
            e.setStatus("ACCEPTED");
            e.setRespondedAt(OffsetDateTime.now());
            jpaRepository.save(e);
        });
    }

    @Override
    public void decline(UUID id) {
        jpaRepository.findById(id).ifPresent(e -> {
            e.setStatus("DECLINED");
            e.setRespondedAt(OffsetDateTime.now());
            jpaRepository.save(e);
        });
    }

    @Override
    public boolean hasPendingInvite(UUID accountId, UUID inviteeId) {
        return jpaRepository.existsByAccountIdAndInviteeIdAndStatus(accountId, inviteeId, "PENDING");
    }
}
