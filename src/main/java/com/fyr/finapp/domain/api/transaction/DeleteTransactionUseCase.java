package com.fyr.finapp.domain.api.transaction;

public interface DeleteTransactionUseCase {
    void delete(String transactionId, String accountId);
}
