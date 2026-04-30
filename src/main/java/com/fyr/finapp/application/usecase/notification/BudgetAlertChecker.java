package com.fyr.finapp.application.usecase.notification;

import com.fyr.finapp.adapters.driven.persistence.jpa.repository.BudgetJpaRepository;
import com.fyr.finapp.domain.spi.notification.INotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

public class BudgetAlertChecker {
    private static final Logger log = LoggerFactory.getLogger(BudgetAlertChecker.class);

    private final BudgetJpaRepository budgetJpaRepository;
    private final INotificationRepository notificationRepository;

    public BudgetAlertChecker(BudgetJpaRepository budgetJpaRepository,
                               INotificationRepository notificationRepository) {
        this.budgetJpaRepository = budgetJpaRepository;
        this.notificationRepository = notificationRepository;
    }

    public void check(UUID userId, UUID categoryId, String categoryName) {
        try {
            var budget = budgetJpaRepository.findByCategoryIdAndUserId(categoryId, userId).orElse(null);
            if (budget == null) return;

            var now = OffsetDateTime.now(ZoneOffset.UTC);
            var periodStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            var periodEnd = periodStart.plusMonths(1);

            var rows = budgetJpaRepository.findAllWithSpent(userId, periodStart, periodEnd);
            var row = rows.stream()
                    .filter(r -> ((UUID) r[0]).equals(budget.getId()))
                    .findFirst().orElse(null);
            if (row == null) return;

            long limit = ((Number) row[2]).longValue();
            long spent = ((Number) row[6]).longValue();
            if (limit <= 0) return;

            double pct = (double) spent / limit * 100;

            if (pct >= 100) {
                notificationRepository.save(new INotificationRepository.SaveCommand(
                        userId, "BUDGET_ALERT",
                        "¡Presupuesto superado!",
                        categoryName + ": has gastado " + (int) pct + "% del límite mensual",
                        Map.of("categoryId", categoryId.toString(), "spent", spent,
                               "limit", limit, "percentage", (int) pct)
                ));
            } else if (pct >= 80) {
                notificationRepository.save(new INotificationRepository.SaveCommand(
                        userId, "BUDGET_ALERT",
                        "Alerta de presupuesto",
                        categoryName + " al " + (int) pct + "% del límite mensual",
                        Map.of("categoryId", categoryId.toString(), "spent", spent,
                               "limit", limit, "percentage", (int) pct)
                ));
            }
        } catch (Exception e) {
            log.warn("Failed to check budget alert for userId={} categoryId={}", userId, categoryId, e);
        }
    }
}
