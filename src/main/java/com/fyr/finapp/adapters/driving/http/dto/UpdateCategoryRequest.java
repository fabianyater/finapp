package com.fyr.finapp.adapters.driving.http.dto;

public record UpdateCategoryRequest(
        String name,
        String icon,
        String color,
        String type
) {
}