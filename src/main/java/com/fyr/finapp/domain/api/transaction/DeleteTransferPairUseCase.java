package com.fyr.finapp.domain.api.transaction;

public interface DeleteTransferPairUseCase {
    void delete(String transactionId, String accountId);
}
