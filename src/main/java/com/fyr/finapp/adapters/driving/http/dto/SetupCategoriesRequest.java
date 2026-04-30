package com.fyr.finapp.adapters.driving.http.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SetupCategoriesRequest(
        @NotNull List<String> keys
) {
}
