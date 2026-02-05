package com.fyr.finapp.adapters.driving.http.dto;

public record CreateAccountRequest(
        String name,
        String type,
        Long initialBalance,
        String icon,
        String color,
        String currency) {
}
