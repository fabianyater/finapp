package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountMemberEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AccountMemberJpaRepository extends JpaRepository<AccountMemberEntity, AccountMemberId> {

    @Query("SELECT m FROM AccountMemberEntity m JOIN FETCH m.user WHERE m.accountId = :accountId")
    List<AccountMemberEntity> findMembersWithUsers(@Param("accountId") UUID accountId);

    boolean existsByAccountIdAndUserId(UUID accountId, UUID userId);

    @Transactional
    void deleteByAccountIdAndUserId(UUID accountId, UUID userId);
}
