package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends
        JpaRepository<TransactionEntity, UUID>,
        JpaSpecificationExecutor<TransactionEntity> {
    Optional<TransactionEntity> findByIdAndAccounts_Id(UUID id, UUID id1);
}
