package com.fyr.finapp.domain.api.recurring;

import com.fyr.finapp.domain.model.recurring.RecurringTransaction;

import java.util.List;

public interface ListRecurringTransactionsUseCase {
    List<RecurringTransaction> list();
}
