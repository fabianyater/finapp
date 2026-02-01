package com.fyr.finapp.adapters.config;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.function.Supplier;

public class TransactionalExecutor {
    private final TransactionTemplate tx;

    public TransactionalExecutor(TransactionTemplate tx) {
        this.tx = Objects.requireNonNull(tx);
    }

    public <T> T required(Supplier<T> action) {
        return tx.execute(status -> action.get());
    }

    public void required(Runnable action) {
        tx.executeWithoutResult(status -> action.run());
    }
}
