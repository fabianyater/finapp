package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByUser_IdAndTypeAndNameAndIsDeletedFalse(UUID id, String type, String name);

    List<CategoryEntity> findAllByUser_IdAndIsDeletedFalse(UUID userId);
}
