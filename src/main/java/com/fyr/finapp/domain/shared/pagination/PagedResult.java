package com.fyr.finapp.domain.shared.pagination;

import java.util.List;
import java.util.function.Function;

public record PagedResult<T>(
        List<T> content,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious) {
    public <R> PagedResult<R> map(Function<T, R> mapper) {
        return new PagedResult<>(
                content.stream().map(mapper).toList(),
                currentPage,
                pageSize,
                totalElements,
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}
