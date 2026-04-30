package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CreateTransferRequest;
import com.fyr.finapp.domain.api.transaction.CreateTransferUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Tag(name = "Transfers", description = "Account transfer endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/transfers")
public class TransferController {
    private final CreateTransferUseCase createTransferUseCase;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody CreateTransferRequest request) {
        var command = new CreateTransferUseCase.Command(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                request.description(),
                request.note(),
                request.occurredOn()
        );

        var result = createTransferUseCase.create(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.outTransactionId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(Map.of(
                        "outTransactionId", result.outTransactionId(),
                        "inTransactionId", result.inTransactionId()
                ));
    }
}
