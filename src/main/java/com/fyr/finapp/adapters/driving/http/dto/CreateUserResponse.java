package com.fyr.finapp.adapters.driving.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateUserResponse", description = "Respuesta con los datos del usuario creado")
public record CreateUserResponse(
        @Schema(
                description = "ID único del usuario",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        String userId,

        @Schema(
                description = "Email del usuario",
                example = "johndoe@finapp.com"
        )
        String email) {
}
