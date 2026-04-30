package com.fyr.finapp.adapters.driven.persistence.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@Entity
@Table(name = "recurring_transactions")
public class RecurringTransactionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotNull
    @ColumnDefault("'COP'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;

    @NotNull
    @Column(name = "frequency", nullable = false, length = 20)
    private String frequency;

    @NotNull
    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "last_generated_at")
    private OffsetDateTime lastGeneratedAt;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private boolean active;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private AccountEntity toAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @PrePersist
    public void prePersist() {
        var now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
