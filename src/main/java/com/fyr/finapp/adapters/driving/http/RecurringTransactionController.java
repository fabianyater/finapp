package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateRecurringTransactionRequest;
import com.fyr.finapp.adapters.driving.http.dto.RecurringTransactionResponse;
import com.fyr.finapp.adapters.driving.http.dto.UpdateRecurringTransactionRequest;
import com.fyr.finapp.domain.api.recurring.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Recurring Transactions", description = "Recurring transaction management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/recurring-transactions")
@PreAuthorize("isAuthenticated()")
public class RecurringTransactionController {
    private final CreateRecurringTransactionUseCase createUseCase;
    private final UpdateRecurringTransactionUseCase updateUseCase;
    private final DeleteRecurringTransactionUseCase deleteUseCase;
    private final ListRecurringTransactionsUseCase listUseCase;
    private final ToggleRecurringTransactionUseCase toggleUseCase;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RecurringTransactionResponse>> list() {
        var result = listUseCase.list().stream()
                .map(RecurringTransactionResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@Valid @RequestBody CreateRecurringTransactionRequest request) {
        var command = new CreateRecurringTransactionUseCase.Command(
                request.accountId(),
                request.toAccountId(),
                request.categoryId(),
                request.type(),
                request.amount(),
                request.description(),
                request.note(),
                request.frequency(),
                request.nextDueDate()
        );
        var result = createUseCase.create(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();

        return ResponseEntity.created(location).body(result.id());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateRecurringTransactionRequest request) {
        var command = new UpdateRecurringTransactionUseCase.Command(
                id,
                request.accountId(),
                request.categoryId(),
                request.type(),
                request.amount(),
                request.description(),
                request.note(),
                request.frequency(),
                request.nextDueDate()
        );
        updateUseCase.update(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable String id) {
        toggleUseCase.toggle(id);
        return ResponseEntity.noContent().build();
    }
}
