package com.fyr.finapp.adapters.driving.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Surname is required")
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,30}$", message = "Surname contains invalid characters")
        String surname,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        @Size(max = 254, message = "Email too long")
        String email,

        @NotBlank
        @Size(min = 10, max = 72, message = "Password must be between 10 and 72 characters")
        String password) {
}
