package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountInvitationJpaRepository extends JpaRepository<AccountInvitationEntity, UUID> {

    @Query("SELECT i FROM AccountInvitationEntity i JOIN FETCH i.account JOIN FETCH i.inviter WHERE i.inviteeId = :inviteeId AND i.status = 'PENDING' ORDER BY i.createdAt DESC")
    List<AccountInvitationEntity> findPendingByInviteeId(@Param("inviteeId") UUID inviteeId);

    boolean existsByAccountIdAndInviteeIdAndStatus(UUID accountId, UUID inviteeId, String status);
}
