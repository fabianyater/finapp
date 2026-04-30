package com.fyr.finapp.adapters.driving.http.dto;

public record UpdateAccountRequest(
        String accountId,
        String name,
        String type,
        Long initialBalance,
        String icon,
        String color,
        boolean defaultAccount,
        boolean excludeFromTotal) {
}
