package com.fyr.finapp.domain.model.transaction;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.transaction.exception.TransactionErrorCode;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.Currency;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.shared.vo.TransactionType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Transaction {
    private final TransactionId id;
    private TransactionType type;
    private Money amount;
    private Currency currency;
    private String description;
    private String note;
    private Instant occurredOn;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private final UserId userId;
    private String creatorName;
    private CategoryId categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private AccountId accountId;
    private AccountId toAccountId;
    private List<String> tags;

    private Transaction(
            TransactionId id,
            TransactionType type,
            Money amount,
            Currency currency,
            String description,
            String note,
            Instant occurredOn,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt,
            UserId userId,
            String creatorName,
            CategoryId categoryId,
            String categoryName,
            String categoryColor,
            String categoryIcon,
            AccountId accountId,
            AccountId toAccountId,
            List<String> tags
    ) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.note = note;
        this.occurredOn = occurredOn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.userId = userId;
        this.creatorName = creatorName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.categoryIcon = categoryIcon;
        this.accountId = accountId;
        this.toAccountId = toAccountId;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public static Transaction create(
            TransactionType type,
            Money amount,
            String description,
            String note,
            Instant occurredOn,
            UserId userId,
            CategoryId categoryId,
            AccountId accountId,
            AccountId toAccountId,
            List<String> tags
    ) {
        Instant now = Instant.now();

        if (amount.isNegative() || amount.isZero()) {
            throw new ValidationException(
                    "Amount cannot be negative",
                    TransactionErrorCode.AMOUNT_NEGATIVE
            );
        }

        if (description == null || description.isBlank()) {
            throw new ValidationException(
                    "Description cannot be empty",
                    TransactionErrorCode.DESCRIPTION_REQUIRED
            );
        }

        return new Transaction(
                TransactionId.generate(),
                type,
                amount,
                amount.currency(),
                description,
                note,
                occurredOn,
                now,
                now,
                null,
                userId,
                null,
                categoryId,
                null, null, null,
                accountId,
                toAccountId,
                tags
        );
    }

    public static Transaction reconstruct(
            TransactionId id,
            TransactionType type,
            Money amount,
            String description,
            String note,
            Instant occurredOn,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt,
            UserId userId,
            String creatorName,
            CategoryId categoryId,
            String categoryName,
            String categoryColor,
            String categoryIcon,
            AccountId accountId,
            AccountId toAccountId,
            List<String> tags
    ) {
        return new Transaction(
                id,
                type,
                amount,
                amount.currency(),
                description,
                note,
                occurredOn,
                createdAt,
                updatedAt,
                deletedAt,
                userId,
                creatorName,
                categoryId,
                categoryName,
                categoryColor,
                categoryIcon,
                accountId,
                toAccountId,
                tags
        );
    }

    public void softDelete() {
        if (isDeleted()) {
            throw new IllegalStateException("Transaction is already deleted");
        }

        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void restore() {
        if (!isDeleted()) throw new IllegalStateException("Transaction is not deleted");
        this.deletedAt = null;
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }


    public void update(
            TransactionType type,
            Money amount,
            String description,
            String note,
            Instant occurredOn,
            CategoryId categoryId,
            AccountId accountId,
            List<String> tags
    ) {
        if (isDeleted()) {
            throw new ValidationException(
                    "Cannot update a deleted transaction",
                    TransactionErrorCode.TRANSACTION_DELETED
            );
        }

        if (amount.isNegative() || amount.isZero()) {
            throw new ValidationException(
                    "Amount cannot be negative",
                    TransactionErrorCode.AMOUNT_NEGATIVE
            );
        }

        if (description == null || description.isBlank()) {
            throw new ValidationException(
                    "Description cannot be empty",
                    TransactionErrorCode.DESCRIPTION_REQUIRED
            );
        }

        this.type = type;
        this.amount = amount;
        this.currency = amount.currency();
        this.description = description;
        this.note = note;
        this.occurredOn = occurredOn;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.updatedAt = Instant.now();
    }

    public void changeType(TransactionType newType) {
        this.type = newType;
        this.updatedAt = Instant.now();
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    public void changeNote(String newNote) {
        this.note = newNote;
        this.updatedAt = Instant.now();
    }

    public void changeOccurredOn(Instant newOccurredOn) {
        this.occurredOn = newOccurredOn;
        this.updatedAt = Instant.now();
    }

    public void changeCategory(CategoryId newCategoryId) {
        this.categoryId = newCategoryId;
        this.updatedAt = Instant.now();
    }

    public void changeAccount(AccountId newAccountId) {
        this.accountId = newAccountId;
        this.updatedAt = Instant.now();
    }

    public void changeAmount(Money newAmount) {
        this.amount = newAmount;
        this.updatedAt = Instant.now();
    }

    public TransactionId getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() { return categoryName; }
    public String getCategoryColor() { return categoryColor; }
    public String getCategoryIcon() { return categoryIcon; }

    public AccountId getAccountId() {
        return accountId;
    }

    public AccountId getToAccountId() {
        return toAccountId;
    }

    public List<String> getTags() {
        return List.copyOf(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
