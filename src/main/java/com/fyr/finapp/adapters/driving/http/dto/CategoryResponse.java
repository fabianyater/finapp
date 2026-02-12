package com.fyr.finapp.adapters.driving.http.dto;

public record CategoryResponse(
        String id,
        String name,
        String type,
        String color,
        String icon,
        String createdAt
) {
}
