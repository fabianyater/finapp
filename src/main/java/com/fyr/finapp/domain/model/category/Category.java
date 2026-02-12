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
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
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
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Category(id, userId, name, type, color, icon, createdAt, updatedAt);
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

    public static List<Category> createDefaultCategoriesForUser(UserId userId) {
        return List.of(
                create(userId, CategoryName.of("Salario"), TransactionType.INCOME, Color.of("#10b981"), Icon.of("currency-dollar")),
                create(userId, CategoryName.of("Freelance"), TransactionType.INCOME, Color.of("#3b82f6"), Icon.of("laptop")),
                create(userId, CategoryName.of("Alimentación"), TransactionType.EXPENSE, Color.of("#ef4444"), Icon.of("utensils")),
                create(userId, CategoryName.of("Transporte"), TransactionType.EXPENSE, Color.of("#f59e0b"), Icon.of("car")),
                create(userId, CategoryName.of("Vivienda"), TransactionType.EXPENSE, Color.of("#06b6d4"), Icon.of("home")),
                create(userId, CategoryName.of("Mascotas"), TransactionType.EXPENSE, Color.of("#8b5cf6"), Icon.of("paw")),
                create(userId, CategoryName.of("Entretenimiento"), TransactionType.EXPENSE, Color.of("#ec4899"), Icon.of("gamepad")),
                create(userId, CategoryName.of("Salud"), TransactionType.EXPENSE, Color.of("#3d4451"), Icon.of("heart-pulse")),
                create(userId, CategoryName.of("Educación"), TransactionType.EXPENSE, Color.of("#14b8a6"), Icon.of("book")),
                create(userId, CategoryName.of("Ropa"), TransactionType.EXPENSE, Color.of("#f97316"), Icon.of("tshirt")),
                create(userId, CategoryName.of("Viajes"), TransactionType.EXPENSE, Color.of("#0ea5e9"), Icon.of("plane")),
                create(userId, CategoryName.of("Regalos"), TransactionType.EXPENSE, Color.of("#db2777"), Icon.of("gift"))
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
