package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.adapters.driving.http.validation.messages.UserValidationMessages;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = UserValidationMessages.EMAIL_REQUIRED)
        String email,
        @NotBlank(message = UserValidationMessages.PASSWORD_REQUIRED)
        String password) {
}
