package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.domain.api.recurring.DeleteRecurringTransactionUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import jakarta.transaction.Transactional;

public class DeleteRecurringTransactionService implements DeleteRecurringTransactionUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IRecurringTransactionRepository recurringTransactionRepository;

    public DeleteRecurringTransactionService(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository) {
        this.authenticationRepository = authenticationRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @Override
    @Transactional
    public void delete(String id) {
        var userId = authenticationRepository.getCurrentUserId();
        var recurringId = RecurringTransactionId.of(id);

        var recurring = recurringTransactionRepository.findById(recurringId, userId)
                .orElseThrow(() -> new ValidationException("Recurring transaction not found", null));

        if (!recurring.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied", null);
        }

        recurring.softDelete();
        recurringTransactionRepository.save(recurring);
    }
}
