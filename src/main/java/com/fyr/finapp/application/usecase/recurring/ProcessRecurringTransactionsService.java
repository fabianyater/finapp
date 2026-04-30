package com.fyr.finapp.application.usecase.recurring;

import com.fyr.finapp.domain.model.recurring.RecurringTransaction;
import com.fyr.finapp.domain.shared.vo.Money;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.spi.account.IAccountRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import com.fyr.finapp.domain.spi.recurring.IRecurringTransactionRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ProcessRecurringTransactionsService {
    private static final Logger log = LoggerFactory.getLogger(ProcessRecurringTransactionsService.class);

    private final IRecurringTransactionRepository recurringTransactionRepository;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final INotificationRepository notificationRepository;

    public ProcessRecurringTransactionsService(
            IRecurringTransactionRepository recurringTransactionRepository,
            ITransactionRepository transactionRepository,
            IAccountRepository accountRepository,
            INotificationRepository notificationRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void process() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> due = recurringTransactionRepository.findDue(today);

        log.info("Processing {} due recurring transactions for date={}", due.size(), today);

        for (RecurringTransaction rt : due) {
            try {
                processOne(rt);
            } catch (Exception e) {
                log.error("Failed to process recurring transaction id={}: {}", rt.getId().value(), e.getMessage(), e);
            }
        }
    }

    private void processOne(RecurringTransaction rt) {
        var account = accountRepository.findById(rt.getAccountId())
                .orElseThrow(() -> new IllegalStateException("Account not found: " + rt.getAccountId().value()));

        if (account.isArchived()) {
            log.warn("Skipping recurring transaction id={} because account is archived", rt.getId().value());
            return;
        }

        Instant now = Instant.now();

        if (rt.getType().isTransfer() && rt.getToAccountId() != null) {
            var toAccount = accountRepository.findById(rt.getToAccountId())
                    .orElseThrow(() -> new IllegalStateException("To-account not found: " + rt.getToAccountId().value()));

            var outTxn = Transaction.create(
                    rt.getType(), rt.getAmount(), rt.getDescription(), rt.getNote(),
                    now, rt.getUserId(), null, rt.getAccountId(), rt.getToAccountId(), null);
            var inAmount = Money.of(rt.getAmount().amount(), toAccount.getCurrency().code());
            var inTxn = Transaction.create(
                    rt.getType(), inAmount, rt.getDescription(), rt.getNote(),
                    now, rt.getUserId(), null, rt.getToAccountId(), null, null);

            account.debit(rt.getAmount());
            toAccount.credit(inAmount);

            transactionRepository.save(outTxn);
            transactionRepository.save(inTxn);
            accountRepository.save(account);
            accountRepository.save(toAccount);

            log.info("Generated transfer outTxn={} inTxn={} from recurring id={}",
                    outTxn.getId().value(), inTxn.getId().value(), rt.getId().value());
        } else {
            var transaction = Transaction.create(
                    rt.getType(), rt.getAmount(), rt.getDescription(), rt.getNote(),
                    now, rt.getUserId(), rt.getCategoryId(), rt.getAccountId(), null, null);

            account.applyTransaction(rt.getType(), rt.getAmount());
            transactionRepository.save(transaction);
            accountRepository.save(account);

            log.info("Generated transaction id={} from recurring id={}", transaction.getId().value(), rt.getId().value());
        }

        try {
            notificationRepository.save(new INotificationRepository.SaveCommand(
                    rt.getUserId().value(), "RECURRING_PROCESSED",
                    "Transacción recurrente procesada",
                    rt.getDescription(),
                    Map.of("recurringId", rt.getId().value().toString(),
                           "amount", rt.getAmount().amount(),
                           "currency", rt.getAmount().currency().code())
            ));
        } catch (Exception e) {
            log.warn("Failed to save notification for recurring transaction {}", rt.getId().value(), e);
        }

        rt.advanceNextDueDate();
        recurringTransactionRepository.save(rt);
    }
}
