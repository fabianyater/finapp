package com.fyr.finapp.domain.common;

import java.time.Instant;
import java.util.Set;

import static com.fyr.finapp.domain.common.constraints.CommonConstraints.*;

//TODO: Ajustar los mensajes de error, las constantes, etc...
public record AccountQuery(Pagination pagination,
                           Sorting sorting,
                           AccountFilters filters) {
    public static AccountQuery of(Pagination pagination, Sorting sorting, AccountFilters filters) {
        return new AccountQuery(
                pagination != null ? pagination : Pagination.defaultPagination(),
                sorting != null ? sorting : Sorting.defaultSorting(),
                filters != null ? filters : AccountFilters.empty()
        );
    }

    public static AccountQuery simple(int page, int size) {
        return new AccountQuery(
                new Pagination(page, size),
                Sorting.defaultSorting(),
                AccountFilters.empty()
        );
    }

    record Pagination(int page, int size) {
        private static final int MIN_PAGE = 0;
        private static final int MIN_SIZE = 1;
        private static final int MAX_SIZE = 100;
        private static final int DEFAULT_SIZE = 20;

        public Pagination {
            if (page < MIN_PAGE) {
                throw new IllegalArgumentException("Page must be >= " + MIN_PAGE);
            }
            if (size < MIN_SIZE || size > MAX_SIZE) {
                throw new IllegalArgumentException("Size must be between " + MIN_SIZE + " and " + MAX_SIZE);
            }
        }

        public static Pagination defaultPagination() {
            return new Pagination(0, DEFAULT_SIZE);
        }

        public static Pagination of(int page, int size) {
            return new Pagination(page, size);
        }
    }

    record Sorting(SortField field, SortDirection direction) {

        public Sorting {
            field = field != null ? field : SortField.CREATED_AT;
            direction = direction != null ? direction : SortDirection.DESC;
        }

        public static Sorting defaultSorting() {
            return new Sorting(SortField.CREATED_AT, SortDirection.DESC);
        }

        public static Sorting by(SortField field, SortDirection direction) {
            return new Sorting(field, direction);
        }

        public static Sorting by(SortField field) {
            return new Sorting(field, SortDirection.ASC);
        }
    }

    enum SortField {
        NAME("name"),
        CREATED_AT("createdAt"),
        UPDATED_AT("updatedAt"),
        INITIAL_BALANCE("initialBalance"),
        TYPE("type");

        private final String fieldName;

        SortField(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public static SortField fromString(String value) {
            if (value == null || value.isBlank()) {
                return CREATED_AT;
            }

            for (SortField field : values()) {
                if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                    return field;
                }
            }

            throw new IllegalArgumentException("Invalid sort field: " + value +
                    ". Valid values: name, createdAt, updatedAt, initialBalance, type");
        }
    }

    enum SortDirection {
        ASC, DESC;

        public static SortDirection fromString(String value) {
            if (value == null || value.isBlank()) {
                return DESC;
            }
            try {
                return valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid sort direction: " + value +
                        ". Valid values: ASC, DESC");
            }
        }
    }

    record AccountFilters(
            Set<String> types,
            Set<String> currencies,
            Boolean isDefault,
            Boolean isArchived,
            Boolean excludeFromTotal,
            String search,
            DateRange createdDateRange,
            BalanceRange balanceRange
    ) {
        public AccountFilters {
            types = types != null ? Set.copyOf(types) : Set.of();
            currencies = currencies != null ? Set.copyOf(currencies) : Set.of();
        }

        public static AccountFilters empty() {
            return new AccountFilters(null, null, null, null, null, null, null, null);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Set<String> types;
            private Set<String> currencies;
            private Boolean isDefault;
            private Boolean isArchived;
            private Boolean excludeFromTotal;
            private String search;
            private DateRange createdDateRange;
            private BalanceRange balanceRange;

            public Builder types(Set<String> types) {
                this.types = types;
                return this;
            }

            public Builder currencies(Set<String> currencies) {
                this.currencies = currencies;
                return this;
            }

            public Builder isDefault(Boolean isDefault) {
                this.isDefault = isDefault;
                return this;
            }

            public Builder isArchived(Boolean isArchived) {
                this.isArchived = isArchived;
                return this;
            }

            public Builder excludeFromTotal(Boolean excludeFromTotal) {
                this.excludeFromTotal = excludeFromTotal;
                return this;
            }

            public Builder search(String search) {
                this.search = search;
                return this;
            }

            public Builder createdDateRange(DateRange createdDateRange) {
                this.createdDateRange = createdDateRange;
                return this;
            }

            public Builder createdDateRange(Instant from, Instant to) {
                this.createdDateRange = DateRange.of(from, to);
                return this;
            }

            public Builder balanceRange(BalanceRange balanceRange) {
                this.balanceRange = balanceRange;
                return this;
            }

            public Builder balanceRange(Long min, Long max) {
                this.balanceRange = BalanceRange.of(min, max);
                return this;
            }

            public AccountFilters build() {
                return new AccountFilters(
                        types, currencies, isDefault, isArchived,
                        excludeFromTotal, search, createdDateRange, balanceRange
                );
            }
        }
    }

    record DateRange(Instant from, Instant to) {
        public DateRange {
            if (from != null && to != null && from.isAfter(to)) {
                throw new IllegalArgumentException("'from' date must be before 'to' date");
            }
        }

        public static DateRange of(Instant from, Instant to) {
            if (from == null && to == null) {
                return null;
            }
            return new DateRange(from, to);
        }
    }

    record BalanceRange(Long min, Long max) {
        public BalanceRange {
            if (min != null && min < 0) {
                throw new IllegalArgumentException("Minimum balance cannot be negative");
            }
            if (max != null && max < 0) {
                throw new IllegalArgumentException("Maximum balance cannot be negative");
            }
            if (min != null && max != null && min > max) {
                throw new IllegalArgumentException("Minimum balance must be <= maximum balance");
            }
        }

        public static BalanceRange of(Long min, Long max) {
            if (min == null && max == null) {
                return null;
            }
            return new BalanceRange(min, max);
        }
    }
}
