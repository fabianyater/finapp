package com.fyr.finapp.domain.api.transaction;

public interface RestoreTransactionUseCase {
    void restore(String transactionId, String accountId);
}
