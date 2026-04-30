package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.domain.api.recurring.ToggleRecurringTransactionUseCase;
import com.fyr.finapp.domain.exception.ForbiddenException;
import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import jakarta.transaction.Transactional;

public class ToggleRecurringTransactionService implements ToggleRecurringTransactionUseCase {

    private final IAuthenticationRepository authenticationRepository;
    private final IRecurringTransactionRepository recurringTransactionRepository;

    public ToggleRecurringTransactionService(
            IAuthenticationRepository authenticationRepository,
            IRecurringTransactionRepository recurringTransactionRepository) {
        this.authenticationRepository = authenticationRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @Override
    @Transactional
    public void toggle(String id) {
        var userId = authenticationRepository.getCurrentUserId();
        var recurringId = RecurringTransactionId.of(id);

        var recurring = recurringTransactionRepository.findById(recurringId, userId)
                .orElseThrow(() -> new ValidationException("Recurring transaction not found", null));

        if (!recurring.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied", null);
        }

        if (recurring.isActive()) {
            recurring.deactivate();
        } else {
            recurring.activate();
        }

        recurringTransactionRepository.save(recurring);
    }
}
