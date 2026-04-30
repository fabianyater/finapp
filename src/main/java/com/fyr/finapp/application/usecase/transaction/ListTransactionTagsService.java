package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.domain.api.transaction.ListTransactionTagsUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;

import java.util.List;

public class ListTransactionTagsService implements ListTransactionTagsUseCase {
    private final ITransactionRepository transactionRepository;
    private final IAuthenticationRepository authenticationRepository;

    public ListTransactionTagsService(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        this.transactionRepository = transactionRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public List<String> list() {
        var userId = authenticationRepository.getCurrentUserId();
        return transactionRepository.findAllTagsByUserId(userId);
    }
}
