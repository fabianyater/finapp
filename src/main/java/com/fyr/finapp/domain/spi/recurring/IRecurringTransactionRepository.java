package com.fyr.finapp.domain.spi.recurring;

import com.fyr.finapp.domain.model.recurring.RecurringTransaction;
import com.fyr.finapp.domain.model.recurring.RecurringTransactionId;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRecurringTransactionRepository {
    void save(RecurringTransaction recurringTransaction);
    Optional<RecurringTransaction> findById(RecurringTransactionId id, UserId userId);
    List<RecurringTransaction> findAllByUserId(UserId userId);
    List<RecurringTransaction> findDue(LocalDate today);
}
