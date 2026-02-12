package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CategoryResponse;
import com.fyr.finapp.adapters.driving.http.dto.CreateAccountRequest;
import com.fyr.finapp.adapters.driving.http.dto.CreateCategoryRequest;
import com.fyr.finapp.adapters.driving.http.dto.UpdateCategoryRequest;
import com.fyr.finapp.domain.api.category.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(name = "Categories", description = "Category management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/categories")
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final RestoreCategoryUseCase restoreCategoryUseCase;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(
            @Valid
            @RequestBody
            @Parameter(
                    description = "Request body for creating a new category",
                    required = true,
                    schema = @Schema(implementation = CreateAccountRequest.class)
            )
            CreateCategoryRequest request) {
        var command = new CreateCategoryUseCase.Command(
                request.name(),
                request.icon(),
                request.color(),
                request.type()
        );

        var result = createCategoryUseCase.create(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(result.id());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list() {
        var categories = listCategoriesUseCase.execute().stream()
                .map(c -> new CategoryResponse(
                        c.id(),
                        c.name(),
                        c.type(),
                        c.color(),
                        c.icon(),
                        c.createdAt()
                ))
                .toList();

        return ResponseEntity.ok(categories);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") String id,
            @Valid
            @RequestBody
            @Parameter(
                    description = "Request body for updating an existing category",
                    required = true,
                    schema = @Schema(implementation = CreateAccountRequest.class)
            )
            UpdateCategoryRequest request) {
        var command = new UpdateCategoryUseCase.Command(
                id,
                request.name(),
                request.type(),
                request.color(),
                request.icon()
        );

        updateCategoryUseCase.update(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity
                .noContent()
                .location(location)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteCategoryUseCase.delete(new DeleteCategoryUseCase.Command(id));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity
                .noContent()
                .location(location)
                .build();
    }

    @Operation(
            summary = "Restore a deleted category",
            description = "Restores a previously deleted category, making it active again"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category restored successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category is not deleted")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable("id") String id) {
        restoreCategoryUseCase.restore(new RestoreCategoryUseCase.Command(id));

        return ResponseEntity
                .noContent()
                .build();
    }
}
