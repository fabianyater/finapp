package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    boolean existsByUser_IdAndName(UUID id, String name);

    List<AccountEntity> findByUser_Id(UUID id);
}