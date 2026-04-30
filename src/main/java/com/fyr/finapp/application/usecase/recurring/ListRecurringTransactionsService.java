package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.domain.api.recurring.ListRecurringTransactionsUseCase;
import com.fyr.finapp.domain.model.recurring.RecurringTransaction;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;

import java.util.List;

public class ListRecurringTransactionsService implements ListRecurringTransactionsUseCase {
    private final IAuthenticationRepository authenticationRepository;
    private final IRecurringTransactionRepository recurringTransactionRepository;

    public ListRecurringTransactionsService(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository) {
        this.authenticationRepository = authenticationRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @Override
    public List<RecurringTransaction> list() {
        var userId = authenticationRepository.getCurrentUserId();
        return recurringTransactionRepository.findAllByUserId(userId);
    }
}
