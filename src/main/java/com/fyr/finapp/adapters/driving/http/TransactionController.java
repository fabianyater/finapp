package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateTransactionRequest;
import com.fyr.finapp.adapters.driving.http.dto.PagedTransactionResponse;
import com.fyr.finapp.adapters.driving.http.dto.UpdateTransactionRequest;
import com.fyr.finapp.domain.api.transaction.CreateTransactionUseCase;
import com.fyr.finapp.domain.api.transaction.ListTransactionUseCase;
import com.fyr.finapp.domain.api.transaction.UpdateTransactionUseCase;
import com.fyr.finapp.domain.shared.pagination.PageRequest;
import com.fyr.finapp.domain.shared.pagination.SortDirection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

@Tag(name = "Transactions", description = "Transaction management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/transactions")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final ListTransactionUseCase listTransactionUseCase;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(
            summary = "List transactions",
            description = "Get paginated list of transactions with filtering, sorting and search capabilities"
    )
    public ResponseEntity<PagedTransactionResponse> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) SortDirection direction,
            @RequestParam(required = false) Set<String> accountIds,
            @RequestParam(required = false) Set<String> categoryIds,
            @RequestParam(required = false) Set<String> types,
            @RequestParam(required = false) String search,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant dateFrom,

            @Parameter(description = "Filter transactions until this date")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant dateTo
    ) {
        var query = new ListTransactionUseCase.Query(
                new PageRequest(page, size, sortBy, direction),
                accountIds,
                categoryIds,
                types,
                search,
                dateFrom,
                dateTo
        );

        var result = listTransactionUseCase.list(query);

        return ResponseEntity.ok(PagedTransactionResponse.from(result));
    }
}
