package com.fyr.finapp.application.usecase.category;

import com.fyr.finapp.domain.api.category.GetCategorySummaryUseCase;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;

import java.util.List;

public class GetCategorySummaryService implements GetCategorySummaryUseCase {
    private final ITransactionRepository transactionRepository;
    private final IAuthenticationRepository authenticationRepository;

    public GetCategorySummaryService(ITransactionRepository transactionRepository,
                                     IAuthenticationRepository authenticationRepository) {
        this.transactionRepository = transactionRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public List<SummaryResult> execute(Query query) {
        var userId = authenticationRepository.getCurrentUserId();

        return transactionRepository
                .findCategorySummary(userId, query.accountId(), query.type(), query.dateFrom(), query.dateTo())
                .stream()
                .map(e -> new SummaryResult(e.categoryId(), e.name(), e.color(), e.icon(), e.total()))
                .toList();
    }
}
