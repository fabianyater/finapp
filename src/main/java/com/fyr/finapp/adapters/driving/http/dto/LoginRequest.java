package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.adapters.driving.http.validation.messages.UserValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciales para autenticación")
public record LoginRequest(
        @Schema(
                description = "Email del usuario",
                example = "johndoe@finapp.com"
        )
        @NotBlank(message = UserValidationMessages.EMAIL_REQUIRED)
        String email,

        @Schema(
                description = "Contraseña del usuario",
                example = "P@ssw0rd-123"
        )
        @NotBlank(message = UserValidationMessages.PASSWORD_REQUIRED)
        String password) {
}
