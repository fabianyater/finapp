package com.fyr.finapp.domain.shared.pagination;

import java.util.Set;

import static com.fyr.finapp.domain.shared.constraints.CommonConstraints.*;

public record PageRequest(
        int page,
        int size,
        String sortBy,
        SortDirection direction) {
    public PageRequest {
        if (page < MIN_PAGE) {
            throw new IllegalArgumentException("Page must be >= 0");
        }
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        direction = direction == null ? SortDirection.DESC : direction;
    }

    public PageRequest withValidatedSortBy(String defaultSort, Set<String> validFields) {
        String validatedSort = sortBy == null || sortBy.isBlank() ? defaultSort : sortBy;

        if (!validFields.contains(validatedSort)) {
            throw new IllegalArgumentException("Invalid sortBy field: " + validatedSort);
        }

        return new PageRequest(page, size, validatedSort, direction);
    }
}
