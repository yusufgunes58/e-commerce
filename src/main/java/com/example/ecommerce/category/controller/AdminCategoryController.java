package com.example.ecommerce.category.controller;

import com.example.ecommerce.category.dto.response.CategoryDeleteInfoResponse;
import com.example.ecommerce.category.entity.Category;
import com.example.ecommerce.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin - Categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> create(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(name));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category name")
    public ResponseEntity<Void> updateName(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        categoryService.updateName(id, name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/delete-info")
    @Operation(summary = "Get category delete information")
    public ResponseEntity<CategoryDeleteInfoResponse> getDeleteInfo(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoryService.getDeleteInfo(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirmed
    ) {
        categoryService.deleteCategory(id, confirmed);
        return ResponseEntity.noContent().build();
    }
}
