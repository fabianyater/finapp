package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateUserRequest;
import com.fyr.finapp.adapters.driving.http.dto.CreateUserResponse;
import com.fyr.finapp.domain.api.user.CreateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/users")
class UserController {
    private final CreateUserUseCase createUserUseCase;

    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema. Retorna el ID del usuario creado y su email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateUserRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario creado exitosamente",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateUserResponse.class)
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
                            responseCode = "409",
                            description = "El usuario ya existe",
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateUserResponse> create(
            @Valid
            @RequestBody
            @Parameter(description = "Create user request payload", required = true, schema = @Schema(implementation = CreateUserRequest.class))
            CreateUserRequest request) {
        var command = new CreateUserUseCase.CreateUserCommand(
                request.name(),
                request.surname(),
                request.email(),
                request.password()
        );

        var result = createUserUseCase.create(command);

        var response = new CreateUserResponse(result.userId(), result.email());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.userId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }
}
