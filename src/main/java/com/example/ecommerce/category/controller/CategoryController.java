package com.example.ecommerce.category.controller;

import com.example.ecommerce.category.dto.response.CategoryDeleteInfoResponse;
import com.example.ecommerce.category.entity.Category;
import com.example.ecommerce.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategoriesOrderAsc() {
        return ResponseEntity.ok(categoryService.getCategoriesOrderAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestParam String name) {
        Category category = categoryService.create(name);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping("/{id}/delete-info")
    @Operation(summary = "Get category delete information")
    public ResponseEntity<CategoryDeleteInfoResponse> getDeleteInfo(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                categoryService.getDeleteInfo(id)
        );
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

    @PutMapping("/{id}")
    @Operation(summary = "Update a category name")
    public ResponseEntity<Void> updateName(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        categoryService.updateName(id, name);

        return ResponseEntity.noContent().build();
    }

}
