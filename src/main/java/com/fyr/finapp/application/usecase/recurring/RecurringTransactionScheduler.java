package com.fyr.finapp.application.usecase.recurring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionScheduler {
    private final ProcessRecurringTransactionsService processService;

    public RecurringTransactionScheduler(ProcessRecurringTransactionsService processService) {
        this.processService = processService;
    }

    @Scheduled(cron = "0 25 1 * * *", zone = "America/Bogota")
    public void trigger() {
        processService.process();
    }
}
