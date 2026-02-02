package com.fyr.finapp.adapters.driving.http.dto;

import com.fyr.finapp.adapters.driving.http.validation.messages.UserValidationMessages;
import com.fyr.finapp.domain.model.user.UserConstraints;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = UserValidationMessages.NAME_REQUIRED)
        String name,

        @NotBlank(message = UserValidationMessages.SURNAME_REQUIRED)
        String surname,

        @NotBlank(message = UserValidationMessages.EMAIL_REQUIRED)
        @Email(message = UserValidationMessages.EMAIL_INVALID)
        @Size(max = UserConstraints.EMAIL_MAX_LENGTH, message = UserValidationMessages.EMAIL_TOO_LONG)
        String email,

        @NotBlank
        @Size(
                min = UserConstraints.PASSWORD_MIN_LENGTH,
                max = UserConstraints.PASSWORD_MAX_LENGTH,
                message = UserValidationMessages.PASSWORD_LENGTH)
        String password) {
}
