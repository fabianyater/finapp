package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.domain.api.transaction.RenameTagUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;

public class RenameTagService implements RenameTagUseCase {
    private final ITransactionRepository transactionRepository;
    private final IAuthenticationRepository authenticationRepository;

    public RenameTagService(ITransactionRepository transactionRepository, IAuthenticationRepository authenticationRepository) {
        this.transactionRepository = transactionRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public void rename(String oldTag, String newTag) {
        var userId = authenticationRepository.getCurrentUserId();
        transactionRepository.renameTag(userId, oldTag, newTag);
    }
}
