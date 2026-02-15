package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateTransactionRequest;
import com.fyr.finapp.adapters.driving.http.dto.UpdateTransactionRequest;
import com.fyr.finapp.domain.api.transaction.CreateTransactionUseCase;
import com.fyr.finapp.domain.api.transaction.UpdateTransactionUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Transactions", description = "Transaction management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/transactions")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(
            @Valid
            @RequestBody
            @Parameter(
                    description = "Request body for creating a new transaction",
                    required = true,
                    schema = @Schema(implementation = CreateTransactionRequest.class)
            )
            CreateTransactionRequest request) {
        var command = new CreateTransactionUseCase.Command(
                request.type(),
                request.amount(),
                request.description(),
                request.note(),
                request.occurredOn(),
                request.categoryId(),
                request.accountId()
        );

        var result = createTransactionUseCase.create(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(result.id());
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody @Valid UpdateTransactionRequest request) {

        var command = new UpdateTransactionUseCase.Command(
                transactionId,
                request.type(),
                request.amount(),
                request.description(),
                request.note(),
                request.occurredOn(),
                request.accountId(),
                request.categoryId()
        );

        updateTransactionUseCase.update(command);

        return ResponseEntity.noContent().build();
    }
}
