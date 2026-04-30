package com.fyr.finapp.adapters.driven.persistence.jpa.dto;

import java.util.UUID;

public record CategorySummaryDto(
        UUID categoryId,
        String name,
        String color,
        String icon,
        Long total
) {
}
