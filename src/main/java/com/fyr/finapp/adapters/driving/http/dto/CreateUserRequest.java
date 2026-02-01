package com.fyr.finapp.adapters.driving.http.dto;

public record CreateUserRequest(
        String name,
        String surname,
        String email,
        String password) {
}
