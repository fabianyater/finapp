package com.fyr.finapp.domain.model.recurring;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.RecurringFrequency;
import com.fyr.finapp.domain.shared.vo.TransactionType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class RecurringTransaction {
    private final RecurringTransactionId id;
    private final UserId userId;
    private AccountId accountId;
    private AccountId toAccountId;
    private CategoryId categoryId;
    private TransactionType type;
    private Money amount;
    private String description;
    private String note;
    private RecurringFrequency frequency;
    private LocalDate nextDueDate;
    private Instant lastGeneratedAt;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private RecurringTransaction(
            RecurringTransactionId id,
            UserId userId,
            AccountId accountId,
            AccountId toAccountId,
            CategoryId categoryId,
            TransactionType type,
            Money amount,
            String description,
            String note,
            RecurringFrequency frequency,
            LocalDate nextDueDate,
            Instant lastGeneratedAt,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.toAccountId = toAccountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.note = note;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
        this.lastGeneratedAt = lastGeneratedAt;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static RecurringTransaction create(
            UserId userId,
            AccountId accountId,
            AccountId toAccountId,
            CategoryId categoryId,
            TransactionType type,
            Money amount,
            String description,
            String note,
            RecurringFrequency frequency,
            LocalDate nextDueDate
    ) {
        if (amount.isNegative() || amount.isZero()) {
            throw new ValidationException("Amount must be positive", null);
        }
        if (description == null || description.isBlank()) {
            throw new ValidationException("Description cannot be empty", null);
        }

        Instant now = Instant.now();
        return new RecurringTransaction(
                RecurringTransactionId.generate(),
                userId, accountId, toAccountId, categoryId,
                type, amount, description, note, frequency, nextDueDate,
                null, true, now, now, null
        );
    }

    public static RecurringTransaction reconstruct(
            RecurringTransactionId id,
            UserId userId,
            AccountId accountId,
            AccountId toAccountId,
            CategoryId categoryId,
            TransactionType type,
            Money amount,
            String description,
            String note,
            RecurringFrequency frequency,
            LocalDate nextDueDate,
            Instant lastGeneratedAt,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new RecurringTransaction(
                id, userId, accountId, toAccountId, categoryId,
                type, amount, description, note, frequency, nextDueDate,
                lastGeneratedAt, active, createdAt, updatedAt, deletedAt
        );
    }

    public void update(
            AccountId accountId,
            CategoryId categoryId,
            TransactionType type,
            Money amount,
            String description,
            String note,
            RecurringFrequency frequency,
            LocalDate nextDueDate
    ) {
        if (amount.isNegative() || amount.isZero()) {
            throw new ValidationException("Amount must be positive", null);
        }
        if (description == null || description.isBlank()) {
            throw new ValidationException("Description cannot be empty", null);
        }
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.note = note;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void advanceNextDueDate() {
        this.nextDueDate = frequency.nextDate(this.nextDueDate);
        this.lastGeneratedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isDue(LocalDate today) {
        return active && !isDeleted() && !nextDueDate.isAfter(today);
    }

    public RecurringTransactionId getId() { return id; }
    public UserId getUserId() { return userId; }
    public AccountId getAccountId() { return accountId; }
    public AccountId getToAccountId() { return toAccountId; }
    public CategoryId getCategoryId() { return categoryId; }
    public TransactionType getType() { return type; }
    public Money getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getNote() { return note; }
    public RecurringFrequency getFrequency() { return frequency; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public Instant getLastGeneratedAt() { return lastGeneratedAt; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecurringTransaction that = (RecurringTransaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
