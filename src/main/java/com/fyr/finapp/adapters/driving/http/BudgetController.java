package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.BudgetResponse;
import com.fyr.finapp.domain.api.budget.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Tag(name = "Budgets", description = "Budget management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/budgets")
public class BudgetController {
    private final CreateBudgetUseCase createBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;
    private final ListBudgetsUseCase listBudgetsUseCase;

    public record CreateBudgetRequest(@NotBlank String categoryId, @Min(1) long limitAmount) {}
    public record UpdateBudgetRequest(@Min(1) long limitAmount) {}

    @PreAuthorize("isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BudgetResponse>> list() {
        var results = listBudgetsUseCase.list().stream().map(BudgetResponse::from).toList();
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody @Valid CreateBudgetRequest request) {
        var result = createBudgetUseCase.create(new CreateBudgetUseCase.Command(request.categoryId(), request.limitAmount()));
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(result.id()).toUri();
        return ResponseEntity.created(location).body(result.id());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{budgetId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable String budgetId,
                                       @RequestBody @Valid UpdateBudgetRequest request) {
        updateBudgetUseCase.update(new UpdateBudgetUseCase.Command(budgetId, request.limitAmount()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> delete(@PathVariable String budgetId) {
        deleteBudgetUseCase.delete(budgetId);
        return ResponseEntity.noContent().build();
    }
}
