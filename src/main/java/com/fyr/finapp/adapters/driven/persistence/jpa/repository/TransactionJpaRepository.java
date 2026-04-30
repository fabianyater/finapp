package com.fyr.finapp.adapters.driven.persistence.jpa.repository;

import com.fyr.finapp.adapters.driven.persistence.jpa.dto.CategorySummaryDto;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends
        JpaRepository<TransactionEntity, UUID>,
        JpaSpecificationExecutor<TransactionEntity> {

    Optional<TransactionEntity> findByIdAndAccounts_Id(UUID id, UUID id1);

    @Query(value = "SELECT * FROM transactions WHERE account_id = :accountId AND deleted_at IS NOT NULL ORDER BY deleted_at DESC", nativeQuery = true)
    List<TransactionEntity> findDeletedByUserIdAndAccountId(@Param("userId") UUID userId, @Param("accountId") UUID accountId);

    @Query(value = "SELECT * FROM transactions WHERE id = :id AND account_id = :accountId AND deleted_at IS NOT NULL", nativeQuery = true)
    Optional<TransactionEntity> findDeletedByIdAndAccountId(@Param("id") UUID id, @Param("accountId") UUID accountId);

    @Query(value = """
            SELECT * FROM transactions
            WHERE user_id = :userId
              AND type = 'TRANSFER'
              AND deleted_at IS NULL
              AND id != :excludeId
              AND (
                  (account_id = :pairedAccountId AND to_account_id IS NULL)
                  OR to_account_id = :pairedAccountId
              )
              AND occurred_on = :occurredOn
              AND amount = :amount
            LIMIT 1
            """, nativeQuery = true)
    Optional<TransactionEntity> findPairedTransfer(
            @Param("userId") UUID userId,
            @Param("excludeId") UUID excludeId,
            @Param("pairedAccountId") UUID pairedAccountId,
            @Param("occurredOn") OffsetDateTime occurredOn,
            @Param("amount") Long amount
    );

    @Query("SELECT DISTINCT t FROM TransactionEntity tx JOIN tx.tags t WHERE tx.user.id = :userId ORDER BY t")
    List<String> findAllTagsByUserId(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE transaction_tags SET tag = :newTag
            WHERE tag = :oldTag
            AND transaction_id IN (SELECT id FROM transactions WHERE user_id = :userId AND deleted_at IS NULL)
            """, nativeQuery = true)
    void renameTag(@Param("userId") UUID userId, @Param("oldTag") String oldTag, @Param("newTag") String newTag);

    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM transaction_tags
            WHERE tag = :tag
            AND transaction_id IN (SELECT id FROM transactions WHERE user_id = :userId AND deleted_at IS NULL)
            """, nativeQuery = true)
    void deleteTag(@Param("userId") UUID userId, @Param("tag") String tag);

    @Query("""
            SELECT new com.fyr.finapp.adapters.driven.persistence.jpa.dto.CategorySummaryDto(
                c.id,
                c.name,
                c.color,
                c.icon,
                SUM(t.amount)
            )
            FROM TransactionEntity t
            JOIN t.categories c
            WHERE t.accounts.id = :accountId
              AND t.type = :type
              AND c.isDeleted = false
              AND t.occurredOn >= :dateFrom
              AND t.occurredOn <= :dateTo
            GROUP BY c.id, c.name, c.color, c.icon
            ORDER BY SUM(t.amount) DESC
            """)
    List<CategorySummaryDto> findCategorySummary(
            @Param("userId") UUID userId,
            @Param("accountId") UUID accountId,
            @Param("type") String type,
            @Param("dateFrom") OffsetDateTime dateFrom,
            @Param("dateTo") OffsetDateTime dateTo
    );
}
