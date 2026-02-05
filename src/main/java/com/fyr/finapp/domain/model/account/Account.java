package com.fyr.finapp.domain.model.account;

import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.common.vo.Color;
import com.fyr.finapp.domain.model.common.vo.Currency;
import com.fyr.finapp.domain.model.common.vo.Icon;
import com.fyr.finapp.domain.model.common.vo.Money;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.time.Instant;
import java.util.Objects;

public class Account {
    private final AccountId id;

    private AccountName name;
    private final AccountType type;
    private final Money initialBalance;
    private Icon icon;
    private Color color;
    private Currency currency;

    private boolean defaultAccount;
    private boolean archived;
    private boolean excludeFromTotal;

    private final Instant createdAt;
    private Instant updatedAt;
    private final UserId userId;

    private Account(
            AccountId id,
            UserId userId,
            AccountName name,
            AccountType type,
            Money initialBalance,
            Icon icon,
            Color color,
            boolean isDefault,
            boolean isArchived,
            boolean excludeFromTotal,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
        this.icon = icon;
        this.color = color;
        this.defaultAccount = isDefault;
        this.archived = isArchived;
        this.excludeFromTotal = excludeFromTotal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Account create(
            UserId userId,
            AccountName name,
            AccountType type,
            Money initialBalance
    ) {
        Instant now = Instant.now();
        return new Account(
                AccountId.generate(),
                userId,
                name,
                type,
                initialBalance,
                type.getDefaultIcon(),
                type.getDefaultColor(),
                false,
                false,
                false,
                now,
                now
        );
    }

    public static Account reconstruct(
            AccountId id,
            UserId userId,
            AccountName name,
            AccountType type,
            Money initialBalance,
            Icon icon,
            Color color,
            boolean isDefault,
            boolean isArchived,
            boolean excludeFromTotal,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Account(
                id,
                userId,
                name,
                type,
                initialBalance,
                icon,
                color,
                isDefault,
                isArchived,
                excludeFromTotal,
                createdAt,
                updatedAt
        );
    }

    public void rename(AccountName newName) {
        this.name = newName;
        this.updatedAt = Instant.now();
    }

    public void changeIcon(Icon newIcon) {
        this.icon = newIcon;
        this.updatedAt = Instant.now();
    }

    public void changeColor(Color newColor) {
        this.color = newColor;
        this.updatedAt = Instant.now();
    }

    public void markAsDefault() {
        this.defaultAccount = true;
        this.updatedAt = Instant.now();
    }

    public void unmarkAsDefault() {
        this.defaultAccount = false;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.archived = true;
        this.updatedAt = Instant.now();
    }

    public void unarchive() {
        this.archived = false;
        this.updatedAt = Instant.now();
    }

    public void excludeFromTotal() {
        this.excludeFromTotal = true;
        this.updatedAt = Instant.now();
    }

    public void includeInTotal() {
        this.excludeFromTotal = false;
        this.updatedAt = Instant.now();
    }

    public AccountId getId() {
        return id;
    }

    public AccountName getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }

    public Icon getIcon() {
        return icon;
    }

    public Color getColor() {
        return color;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isDefaultAccount() {
        return defaultAccount;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isExcludeFromTotal() {
        return excludeFromTotal;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
