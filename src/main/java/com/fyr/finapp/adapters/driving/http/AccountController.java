package com.fyr.finapp.adapters.driving.http;


import com.fyr.finapp.adapters.driving.http.dto.CreateAccountRequest;
import com.fyr.finapp.adapters.driving.http.dto.CreateAccountResponse;
import com.fyr.finapp.domain.api.account.CreateAccountUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Accounts", description = "Account management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/accounts")
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;


    @Operation(
            summary = "Create a new account",
            description = "Creates a new financial account for the authenticated user. The account can be of type CASH, BANK, or CREDIT_CARD. " +
                    "If this is the user's first account, it will automatically be marked as default. " +
                    "CASH and BANK accounts cannot have negative initial balance."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreateAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., invalid currency code, negative balance for CASH/BANK account)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "An account with the same name already exists for this user",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateAccountResponse> create(
            @Valid
            @RequestBody
            @Parameter(
                    description = "Account details including name, type (CASH, BANK, CREDIT_CARD), initial balance in cents, " +
                            "icon name, color (hex format #RRGGBB), and currency code (3 uppercase letters, e.g., COP, USD, EUR)",
                    required = true,
                    schema = @Schema(implementation = CreateAccountRequest.class)
            )
            CreateAccountRequest request) {
        var command = new CreateAccountUseCase.Command(
                request.name(),
                request.type(),
                request.initialBalance(),
                request.icon(),
                request.color(),
                request.currency()
        );

        var result = createAccountUseCase.create(command);

        var response = new CreateAccountResponse(result.id());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }
}
