package com.fyr.finapp.domain.model.budget;

import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.time.Instant;

public class Budget {
    private final BudgetId id;
    private final UserId userId;
    private final CategoryId categoryId;
    private long limitAmount;
    private final Instant createdAt;
    private Instant updatedAt;

    private Budget(BudgetId id, UserId userId, CategoryId categoryId, long limitAmount,
                   Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Budget create(UserId userId, CategoryId categoryId, long limitAmount) {
        Instant now = Instant.now();
        return new Budget(BudgetId.generate(), userId, categoryId, limitAmount, now, now);
    }

    public static Budget reconstruct(BudgetId id, UserId userId, CategoryId categoryId,
                                     long limitAmount, Instant createdAt, Instant updatedAt) {
        return new Budget(id, userId, categoryId, limitAmount, createdAt, updatedAt);
    }

    public void updateLimit(long newLimit) {
        this.limitAmount = newLimit;
        this.updatedAt = Instant.now();
    }

    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    public BudgetId getId() { return id; }
    public UserId getUserId() { return userId; }
    public CategoryId getCategoryId() { return categoryId; }
    public long getLimitAmount() { return limitAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
