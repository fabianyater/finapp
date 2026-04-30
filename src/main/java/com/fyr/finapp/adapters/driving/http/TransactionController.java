package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateTransactionRequest;
import com.fyr.finapp.adapters.driving.http.dto.DeletedTransactionResponse;
import com.fyr.finapp.adapters.driving.http.dto.PagedTransactionResponse;
import com.fyr.finapp.adapters.driving.http.dto.TransactionResponse;
import com.fyr.finapp.adapters.driving.http.dto.UpdateTransactionRequest;
import com.fyr.finapp.domain.api.transaction.*;
import org.springframework.http.HttpHeaders;
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
import java.util.List;
import java.util.Set;

@Tag(name = "Transactions", description = "Transaction management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/accounts")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final ListTransactionUseCase listTransactionUseCase;
    private final ListTransactionTagsUseCase listTransactionTagsUseCase;
    private final RenameTagUseCase renameTagUseCase;
    private final DeleteTagUseCase deleteTagUseCase;
    private final TransactionDetailsUseCase transactionDetailsUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final ListDeletedTransactionsUseCase listDeletedTransactionsUseCase;
    private final RestoreTransactionUseCase restoreTransactionUseCase;
    private final DeleteTransferPairUseCase deleteTransferPairUseCase;
    private final ExportTransactionsCsvUseCase exportTransactionsCsvUseCase;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{accountId}/transactions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(
            @PathVariable String accountId,
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
                accountId,
                request.tags()
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

    @PutMapping("/{accountId}/transactions/{transactionId}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable String accountId,
            @PathVariable String transactionId,
            @RequestBody @Valid UpdateTransactionRequest request) {

        var command = new UpdateTransactionUseCase.Command(
                transactionId,
                request.type(),
                request.amount(),
                request.description(),
                request.note(),
                request.occurredOn(),
                accountId,
                request.categoryId(),
                request.tags()
        );

        updateTransactionUseCase.update(command);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/transactions")
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
            Instant dateTo,

            @RequestParam(required = false) Set<String> tags
    ) {
        var query = new ListTransactionUseCase.Query(
                new PageRequest(page, size, sortBy, direction),
                accountIds,
                categoryIds,
                types,
                search,
                dateFrom,
                dateTo,
                tags
        );

        var result = listTransactionUseCase.list(query);

        return ResponseEntity.ok(PagedTransactionResponse.from(result));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountId}/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> get(@PathVariable String transactionId, @PathVariable String accountId) {
        var result = transactionDetailsUseCase.getTransactionDetails(transactionId, accountId);

        return ResponseEntity.ok(TransactionResponse.from(result));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{accountId}/transactions/{txnId}")
    public ResponseEntity<Void> delete(
            @PathVariable String accountId,
            @PathVariable String txnId) {
        deleteTransactionUseCase.delete(txnId, accountId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{accountId}/transactions/{txnId}/transfer")
    public ResponseEntity<Void> deleteTransfer(
            @PathVariable String accountId,
            @PathVariable String txnId) {
        deleteTransferPairUseCase.delete(txnId, accountId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountId}/transactions/deleted")
    public ResponseEntity<List<DeletedTransactionResponse>> listDeleted(@PathVariable String accountId) {
        var results = listDeletedTransactionsUseCase.execute(accountId);
        var response = results.stream().map(DeletedTransactionResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/transactions/tags")
    public ResponseEntity<List<String>> listTags() {
        return ResponseEntity.ok(listTransactionTagsUseCase.list());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/transactions/tags/{tag}")
    public ResponseEntity<Void> renameTag(
            @PathVariable String tag,
            @RequestBody RenameTagRequest request) {
        renameTagUseCase.rename(tag, request.newName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/transactions/tags/{tag}")
    public ResponseEntity<Void> deleteTag(@PathVariable String tag) {
        deleteTagUseCase.delete(tag);
        return ResponseEntity.noContent().build();
    }

    record RenameTagRequest(String newName) {}

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/transactions/export")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) Set<String> accountIds,
            @RequestParam(required = false) Set<String> categoryIds,
            @RequestParam(required = false) Set<String> types,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo) {

        var query = new ExportTransactionsCsvUseCase.Query(accountIds, categoryIds, types, search, dateFrom, dateTo);
        String csv = exportTransactionsCsvUseCase.export(query);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions.csv\"")
                .body(csv);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{accountId}/transactions/{txnId}/restore")
    public ResponseEntity<Void> restore(
            @PathVariable String accountId,
            @PathVariable String txnId) {
        restoreTransactionUseCase.restore(txnId, accountId);
        return ResponseEntity.noContent().build();
    }
}
