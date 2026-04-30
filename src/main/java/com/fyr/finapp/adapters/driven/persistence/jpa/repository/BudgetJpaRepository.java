package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetJpaRepository extends JpaRepository<BudgetEntity, UUID> {

    Optional<BudgetEntity> findByCategoryIdAndUserId(UUID categoryId, UUID userId);

    @Query(value = """
            SELECT b.id, b.category_id, b.limit_amount,
                   c.name, c.color, c.icon,
                   COALESCE(SUM(t.amount), 0) AS spent_amount
            FROM budgets b
            JOIN categories c ON c.id = b.category_id
            LEFT JOIN transactions t ON t.category_id = b.category_id
                AND t.user_id = b.user_id
                AND t.type = 'EXPENSE'
                AND t.deleted_at IS NULL
                AND t.occurred_on >= :periodStart
                AND t.occurred_on < :periodEnd
            WHERE b.user_id = :userId
            GROUP BY b.id, b.category_id, b.limit_amount, c.name, c.color, c.icon
            ORDER BY b.created_at
            """, nativeQuery = true)
    List<Object[]> findAllWithSpent(
            @Param("userId") UUID userId,
            @Param("periodStart") OffsetDateTime periodStart,
            @Param("periodEnd") OffsetDateTime periodEnd
    );
}
