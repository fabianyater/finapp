package com.fyr.finapp.domain.spi.transaction;

import com.fyr.finapp.domain.model.transaction.Transaction;

public interface ITransactionRepository {
    void save(Transaction transaction);
}
