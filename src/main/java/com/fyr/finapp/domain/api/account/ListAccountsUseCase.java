package com.fyr.finapp.domain.api.account;

import com.fyr.finapp.domain.shared.pagination.PageRequest;
import com.fyr.finapp.domain.shared.pagination.PagedResult;

import java.time.Instant;
import java.util.Set;

//TODO: Ajustar los mensajes de error, las constantes, etc...
public interface ListAccountsUseCase {
    PagedResult<AccountResult> execute(AccountQuery query);

    record AccountQuery(
            PageRequest pageRequest,
            Set<String> types,
            String search,
            Instant createdAfter,
            Instant createdBefore
    ) {
        private static final Set<String> VALID_SORT_FIELDS = Set.of("name", "createdAt", "type");

        public AccountQuery {
            types = types == null ? Set.of() : types;

            pageRequest = pageRequest.withValidatedSortBy("createdAt", VALID_SORT_FIELDS);
        }

        public static AccountQuery simple(int page, int size) {
            return new AccountQuery(
                    new PageRequest(page, size, "createdAt", null),
                    Set.of(), null, null, null
            );
        }
    }

    record AccountResult(
            String id,
            String name,
            String type,
            long initialBalance,
            long currentBalance,
            String currency,
            String icon,
            String color,
            boolean isDefault,
            boolean isArchived,
            boolean excludeFromTotal,
            Instant createdAt,
            Instant updatedAt) {
    }
}
