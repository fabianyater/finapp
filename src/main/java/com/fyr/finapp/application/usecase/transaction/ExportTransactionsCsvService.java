package com.fyr.finapp.application.usecase.transaction;

import com.fyr.finapp.domain.api.transaction.ExportTransactionsCsvUseCase;
import com.fyr.finapp.domain.model.transaction.Transaction;
import com.fyr.finapp.domain.spi.auth.IAuthenticationRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository;
import com.fyr.finapp.domain.spi.transaction.ITransactionRepository.TransactionFilters;

import java.util.List;

public class ExportTransactionsCsvService implements ExportTransactionsCsvUseCase {
    private static final String HEADER = "id,type,amount,description,note,occurredOn,accountId,categoryId,toAccountId\n";

    private final ITransactionRepository transactionRepository;
    private final IAuthenticationRepository authenticationRepository;

    public ExportTransactionsCsvService(
            ITransactionRepository transactionRepository,
            IAuthenticationRepository authenticationRepository) {
        this.transactionRepository = transactionRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public String export(Query query) {
        var userId = authenticationRepository.getCurrentUserId();
        var filters = new TransactionFilters(
                null,
                query.accountIds(),
                query.categoryIds(),
                query.types(),
                query.search(),
                query.dateFrom(),
                query.dateTo(),
                null
        );
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId, filters);

        var sb = new StringBuilder(HEADER);
        for (Transaction t : transactions) {
            sb.append(escape(t.getId().value().toString())).append(',')
              .append(escape(t.getType().name())).append(',')
              .append(t.getAmount().amount()).append(',')
              .append(escape(t.getDescription())).append(',')
              .append(escape(t.getNote())).append(',')
              .append(escape(t.getOccurredOn().toString())).append(',')
              .append(escape(t.getAccountId().value().toString())).append(',')
              .append(escape(t.getCategoryId() != null ? t.getCategoryId().value().toString() : null)).append(',')
              .append(escape(t.getToAccountId() != null ? t.getToAccountId().value().toString() : null))
              .append('\n');
        }
        return sb.toString();
    }

    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
