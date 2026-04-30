package com.fyr.finapp.adapters.driving.http.dto;

public record CreateCategoryRequest(
        String name,
        String icon,
        String color,
        String type
) {
}
