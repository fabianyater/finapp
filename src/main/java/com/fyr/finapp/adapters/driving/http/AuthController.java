package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.LoginRequest;
import com.fyr.finapp.adapters.driving.http.dto.RefreshTokenRequest;
import com.fyr.finapp.domain.api.auth.AuthenticateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Authentication related endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/auth")
class AuthController {
    private final AuthenticateUseCase authenticateUseCase;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario por email y password. Retorna un token (JWT) y metadata asociada.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticación exitosa",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticateUseCase.AuthResult.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (validación)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorHandler.ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales inválidas",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorHandler.ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error inesperado",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorHandler.ApiError.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticateUseCase.AuthResult> login(
            @Valid
            @RequestBody
            @Parameter(description = "Login request payload", required = true, schema = @Schema(implementation = LoginRequest.class))
            LoginRequest request
    ) {
        var cmd = new AuthenticateUseCase.LoginCommand(request.email(), request.password());
        var result = authenticateUseCase.authenticate(cmd);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticateUseCase.AuthResult> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        var result = authenticateUseCase.refresh(request.refreshToken());
        return ResponseEntity.ok(result);
    }
}
