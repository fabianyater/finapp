package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.adapters.driving.http.validation.messages.UserValidationMessages;
import com.fyr.finapp.domain.model.user.UserConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateUserRequest", description = "Datos para crear un nuevo usuario")
public record CreateUserRequest(
        @Schema(
                description = "Nombre del usuario",
                example = "John"
        )
        @NotBlank(message = UserValidationMessages.NAME_REQUIRED)
        String name,

        @Schema(
                description = "Apellido del usuario",
                example = "Doe"
        )
        @NotBlank(message = UserValidationMessages.SURNAME_REQUIRED)
        String surname,

        @Schema(
                description = "Email del usuario",
                example = "johndoe@finapp.com"
        )
        @NotBlank(message = UserValidationMessages.EMAIL_REQUIRED)
        @Email(message = UserValidationMessages.EMAIL_INVALID)
        @Size(max = UserConstraints.EMAIL_MAX_LENGTH, message = UserValidationMessages.EMAIL_TOO_LONG)
        String email,

        @Schema(
                description = "Contraseña del usuario",
                example = "P@ssw0rd-123"
        )
        @NotBlank
        @Size(
                min = UserConstraints.PASSWORD_MIN_LENGTH,
                max = UserConstraints.PASSWORD_MAX_LENGTH,
                message = UserValidationMessages.PASSWORD_LENGTH)
        String password) {
}
