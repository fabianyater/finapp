package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountJpaRepository extends
        JpaRepository<AccountEntity, UUID>,
        JpaSpecificationExecutor<AccountEntity> {
    boolean existsByUser_IdAndName(UUID id, String name);

    Page<AccountEntity> findByUser_Id(UUID id, Pageable pageable);

}