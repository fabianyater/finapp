package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.RecurringTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringTransactionJpaRepository extends JpaRepository<RecurringTransactionEntity, UUID> {

    List<RecurringTransactionEntity> findAllByUser_Id(UUID userId);

    Optional<RecurringTransactionEntity> findByIdAndUser_Id(UUID id, UUID userId);

    @Query(value = """
            SELECT * FROM recurring_transactions
            WHERE is_active = true
              AND deleted_at IS NULL
              AND next_due_date <= :today
            """, nativeQuery = true)
    List<RecurringTransactionEntity> findDue(@Param("today") LocalDate today);
}
