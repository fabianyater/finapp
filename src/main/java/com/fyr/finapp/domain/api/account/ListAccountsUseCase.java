package com.fyr.finapp.domain.api.account;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.fyr.finapp.domain.shared.constraints.CommonConstraints.*;

//TODO: Ajustar los mensajes de error, las constantes, etc...
public interface ListAccountsUseCase {
    PagedAccountResult execute(AccountQuery query);

    record AccountQuery(
            int page,
            int size,
            String sortBy,
            SortDirection direction,
            Set<String> types,
            String search,
            Instant createdAfter,
            Instant createdBefore
    ) {
        public AccountQuery {
            if (page < MIN_PAGE) {
                throw new IllegalArgumentException("Page must be >= 0");
            }
            if (size < MIN_SIZE || size > MAX_SIZE) {
                throw new IllegalArgumentException("Size must be between 1 and 100");
            }

            sortBy = sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy;
            direction = direction == null ? SortDirection.DESC : direction;
            types = types == null ? Set.of() : types;


            Set<String> validSortFields = Set.of("name", "createdAt", "type");
            if (!validSortFields.contains(sortBy)) {
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
            }
        }

        public static AccountQuery simple(int page, int size) {
            return new AccountQuery(
                    page, size, "createdAt", SortDirection.DESC,
                    Set.of(), null, null, null
            );
        }
    }

    enum SortDirection {
        ASC, DESC
    }

    record PagedAccountResult(
            List<AccountResult> accounts,
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious,
            AccountQuery appliedFilters  // útil para el frontend
    ) {
    }

    record AccountResult(
            String id,
            String name,
            String type,
            long initialBalance,
            String currency,
            String icon,
            String color,
            boolean isDefault,
            boolean isArchived,
            boolean excludeFromTotal,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
