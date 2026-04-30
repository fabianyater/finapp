package com.fyr.finapp.adapters.driving.http.dto;

public record CategorySummaryResponse(
        String categoryId,
        String name,
        String color,
        String icon,
        long total
) {
}
