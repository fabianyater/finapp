package com.fyr.finapp.adapters.driven.persistence.jpa.entity;

import jakarta.persistence.*;
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
@Table(name = "user_preferences")
public class UserPreferenceEntity {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @ColumnDefault("'es-CO'")
    @Column(name = "locale", nullable = false, length = 32)
    private String locale;

    @ColumnDefault("'COP'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @ColumnDefault("'America/Bogota'")
    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    @ColumnDefault("false")
    @Column(name = "dark_mode", nullable = false)
    private Boolean darkMode;

    @ColumnDefault("1")
    @Column(name = "first_day_of_week", nullable = false)
    private Short firstDayOfWeek;

    @ColumnDefault("'yyyy-MM-dd'")
    @Column(name = "date_format", nullable = false, length = 20)
    private String dateFormat;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

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