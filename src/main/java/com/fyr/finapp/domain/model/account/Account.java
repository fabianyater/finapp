package com.fyr.finapp.domain.model.account;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.*;

import java.time.Instant;
import java.util.Objects;

public class Account {
    private final AccountId id;
    private final UserId userId;
    private final Instant createdAt;
    private final Currency currency;

    private AccountName name;
    private AccountType type;
    private Money initialBalance;
    private Money currentBalance;
    private Icon icon;
    private Color color;

    private boolean defaultAccount;
    private boolean archived;
    private boolean excludeFromTotal;

    private Instant updatedAt;

    private Account(
            AccountId id,
            UserId userId,
            Currency currency,
            AccountName name,
            AccountType type,
            Money initialBalance,
            Money currentBalance,
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
        this.currency = currency;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
        this.currentBalance = currentBalance;
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
                initialBalance.currency(),
                name,
                type,
                initialBalance,
                initialBalance,
                type.getDefaultIcon(),
                type.getDefaultColor(),
                false,
                false,
                false,
                now,
                now);
    }

    public static Account reconstruct(
            AccountId id,
            UserId userId,
            AccountName name,
            AccountType type,
            Money initialBalance,
            Money currentBalance,
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
                initialBalance.currency(),
                name,
                type,
                initialBalance,
                currentBalance,
                icon,
                color,
                isDefault,
                isArchived,
                excludeFromTotal,
                createdAt,
                updatedAt);
    }

    public static Account createDefaultAccountForUser(UserId userId) {
        Instant now = Instant.now();
        Currency currency = Currency.of("COP");
        return new Account(
                AccountId.generate(),
                userId,
                currency,
                new AccountName("Efectivo"),
                AccountType.CASH,
                Money.zero(currency),
                Money.zero(currency),
                AccountType.CASH.getDefaultIcon(),
                AccountType.CASH.getDefaultColor(),
                true,
                false,
                false,
                now,
                now);
    }

    public void update(
            AccountName newName,
            AccountType newType,
            Money newInitialBalance,
            Icon newIcon,
            Color newColor
    ) {
        this.name = newName;
        this.type = newType;
        this.initialBalance = newInitialBalance;
        this.icon = newIcon;
        this.color = newColor;
        this.updatedAt = Instant.now();
    }

    public void applyTransaction(TransactionType type, Money amount) {
        validateCurrency(amount);

        this.currentBalance = switch (type) {
            case INCOME -> this.currentBalance.add(amount);
            case EXPENSE -> this.currentBalance.subtract(amount);
        };
    }

    public void reverseTransaction(TransactionType type, Money amount) {
        validateCurrency(amount);

        this.currentBalance = switch (type) {
            case INCOME -> this.currentBalance.subtract(amount);
            case EXPENSE -> this.currentBalance.add(amount);
        };
    }

    /**
     * Simula cómo quedaría el balance después de revertir una transacción,
     * SIN mutar el estado de la cuenta.
     *
     * @param type   Tipo de la transacción original
     * @param amount Monto de la transacción original
     * @return Balance proyectado después de la reversión
     */
    public Money simulateReversal(TransactionType type, Money amount) {
        validateCurrency(amount);

        return switch (type) {
            case INCOME -> this.currentBalance.subtract(amount);
            case EXPENSE -> this.currentBalance.add(amount);
        };
    }

    /**
     * Simula cómo quedaría el balance después de aplicar una transacción,
     * SIN mutar el estado de la cuenta.
     *
     * @param type   Tipo de la nueva transacción
     * @param amount Monto de la nueva transacción
     * @return Balance proyectado después de aplicar la transacción
     */
    public Money simulateApplication(TransactionType type, Money amount) {
        validateCurrency(amount);

        return switch (type) {
            case INCOME -> this.currentBalance.add(amount);
            case EXPENSE -> this.currentBalance.subtract(amount);
        };
    }

    /**
     * Simula el balance completo después de revertir una transacción antigua
     * y aplicar una nueva transacción, SIN mutar el estado.
     * Útil para validar fondos suficientes antes de actualizar transacciones.
     *
     * @param oldType   Tipo de la transacción original
     * @param oldAmount Monto de la transacción original
     * @param newType   Tipo de la nueva transacción
     * @param newAmount Monto de la nueva transacción
     * @return Balance proyectado final
     */
    public Money projectedBalanceAfterUpdate(
            TransactionType oldType,
            Money oldAmount,
            TransactionType newType,
            Money newAmount) {

        validateCurrency(oldAmount);
        validateCurrency(newAmount);

        Money balanceAfterReversal = switch (oldType) {
            case INCOME -> this.currentBalance.subtract(oldAmount);
            case EXPENSE -> this.currentBalance.add(oldAmount);
        };

        return switch (newType) {
            case INCOME -> balanceAfterReversal.add(newAmount);
            case EXPENSE -> balanceAfterReversal.subtract(newAmount);
        };
    }

    /**
     * Verifica si hay fondos suficientes para aplicar un gasto,
     * considerando el estado actual de la cuenta.
     *
     * @param amount Monto del gasto a validar
     * @return true si hay fondos suficientes, false en caso contrario
     */
    public boolean hasSufficientFundsForExpense(Money amount) {
        validateCurrency(amount);

        return this.currentBalance.subtract(amount).isPositiveOrZero();
    }

    /**
     * Verifica si hay fondos suficientes después de revertir una transacción
     * y aplicar una nueva transacción de tipo EXPENSE.
     *
     * @param oldType Tipo de la transacción a revertir
     * @param oldAmount Monto de la transacción a revertir
     * @param newExpenseAmount Monto del nuevo gasto
     * @return true si hay fondos suficientes, false en caso contrario
     */
    public boolean hasSufficientFundsForUpdate(
            TransactionType oldType,
            Money oldAmount,
            Money newExpenseAmount) {

        Money projectedBalance = projectedBalanceAfterUpdate(
                oldType,
                oldAmount,
                TransactionType.EXPENSE,
                newExpenseAmount
        );

        return projectedBalance.isPositiveOrZero();
    }

    public void validateCurrency(Money amount) {
        if (!amount.currency().equals(this.currency)) {
            throw new ValidationException(
                    "Transaction currency must match account currency",
                    AccountErrorCode.CURRENCY_MISMATCH
            );
        }
    }

    public void changeType(AccountType newType) {
        this.type = newType;
        this.icon = newType.getDefaultIcon();
        this.color = newType.getDefaultColor();
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

    public Money getCurrentBalance() {
        return currentBalance;
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
