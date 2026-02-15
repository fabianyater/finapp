package com.fyr.finapp.domain.spi.transaction;

import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.model.transaction.TransactionId;

import java.util.Optional;

public interface ITransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findById(TransactionId id);
}
