package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.adapters.driving.http.dto.CategoryResponse;
import com.fyr.finapp.adapters.driving.http.dto.CreateAccountRequest;
import com.fyr.finapp.adapters.driving.http.dto.CreateCategoryRequest;
import com.fyr.finapp.domain.api.category.CreateCategoryUseCase;
import com.fyr.finapp.domain.api.category.ListCategoriesUseCase;
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
import java.util.List;

@Tag(name = "Categories", description = "Category management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/categories")
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

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
}
