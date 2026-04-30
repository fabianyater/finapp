package com.fyr.finapp.adapters.driven.persistence.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private String type;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "initial_balance", nullable = false)
    private Long initialBalance;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "current_balance", nullable = false)
    private Long currentBalance;

    @Size(max = 3)
    @NotNull
    @ColumnDefault("'COP'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @ColumnDefault("'wallet'")
    @Column(name = "icon", nullable = false, length = Integer.MAX_VALUE)
    private String icon;

    @Size(max = 7)
    @NotNull
    @ColumnDefault("'#004ab3'")
    @Column(name = "color", nullable = false, length = 7)
    private String color;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "exclude_from_total", nullable = false)
    private Boolean excludeFromTotal;

    @Version
    @Column(nullable = false)
    private Long version;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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