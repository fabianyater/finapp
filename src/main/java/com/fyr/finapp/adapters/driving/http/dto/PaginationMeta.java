package com.fyr.finapp.adapters.driving.http.dto;

public record PaginationMeta(
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious) {
}
