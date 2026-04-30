package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.domain.api.transaction.DeleteTagUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;

public class DeleteTagService implements DeleteTagUseCase {
    private final ITransactionRepository transactionRepository;
    private final IAuthenticationRepository authenticationRepository;

    public DeleteTagService(ITransactionRepository transactionRepository, IAuthenticationRepository authenticationRepository) {
        this.transactionRepository = transactionRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public void delete(String tag) {
        var userId = authenticationRepository.getCurrentUserId();
        transactionRepository.deleteTag(userId, tag);
    }
}
