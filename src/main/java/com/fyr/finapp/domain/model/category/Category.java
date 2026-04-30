package com.fyr.finapp.domain.model.category;

import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Color;
import com.fyr.finapp.domain.shared.vo.Icon;
import com.fyr.finapp.domain.shared.vo.TransactionType;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Category {
    private final CategoryId id;

    private CategoryName name;
    private TransactionType type;
    private Color color;
    private Icon icon;
    private boolean deleted;
    private final Instant createdAt;

    private Instant updatedAt;
    private final UserId userId;

    private Category(
            CategoryId id,
            UserId userId,
            CategoryName name,
            TransactionType type,
            Color color,
            Icon icon,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Category create(
            UserId userId,
            CategoryName name,
            TransactionType type,
            Color color,
            Icon icon
    ) {
        return new Category(
                CategoryId.generate(),
                userId,
                name,
                type,
                color,
                icon,
                false,
                Instant.now(),
                Instant.now()
        );
    }

    // Factory method para reconstruir desde BD
    public static Category reconstruct(
            CategoryId id,
            UserId userId,
            CategoryName name,
            TransactionType type,
            Color color,
            Icon icon,
            boolean isDeleted,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Category(id, userId, name, type, color, icon, isDeleted, createdAt, updatedAt);
    }

    public void update(
            CategoryName name,
            TransactionType type,
            Color color,
            Icon icon
    ) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.updatedAt = Instant.now();
    }

    public static Category createFromTemplate(UserId userId, CategoryTemplate template) {
        return create(
                userId,
                CategoryName.of(template.name()),
                TransactionType.fromString(template.type()),
                Color.of(template.color()),
                Icon.of(template.icon())
        );
    }

    public void updateName(CategoryName newName) {
        this.name = newName;
        this.updatedAt = Instant.now();
    }

    public void updateColor(Color newColor) {
        this.color = newColor;
        this.updatedAt = Instant.now();
    }

    public void updateIcon(Icon newIcon) {
        this.icon = newIcon;
        this.updatedAt = Instant.now();
    }

    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    public boolean markAsDeleted() {
        if (this.deleted) {
            return false;
        }

        this.deleted = true;
        this.updatedAt = Instant.now();

        return true;
    }

    public boolean restore() {
        if (!this.deleted) {
            return false;
        }

        this.deleted = false;
        this.updatedAt = Instant.now();
        return true;
    }


    // Getters
    public CategoryId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public CategoryName getName() {
        return name;
    }

    public TransactionType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
