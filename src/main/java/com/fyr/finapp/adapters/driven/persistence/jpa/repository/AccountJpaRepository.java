package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountJpaRepository extends
        JpaRepository<AccountEntity, UUID>,
        JpaSpecificationExecutor<AccountEntity> {
    boolean existsByUser_IdAndNameAllIgnoreCase(UUID id, String name);

    @Modifying
    @Query("UPDATE AccountEntity a SET a.isDefault = false WHERE a.user.id = :userId AND a.isDefault = true")
    int unmarkAllAsDefault(@Param("userId") UUID userId);

    List<AccountEntity> findByUser_Id(UUID id);

    @Query("SELECT a FROM AccountEntity a WHERE a.user.id = :userId OR a.id IN (SELECT m.accountId FROM AccountMemberEntity m WHERE m.userId = :userId)")
    List<AccountEntity> findAllAccessibleByUserId(@Param("userId") UUID userId);
}