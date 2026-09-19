package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.dto.response.customer.ProductListResponse;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping
    @Operation(summary = "Get All Products(active=true) and soring created_at DESC + if stock=0, write end of list")
    public ResponseEntity<Slice<ProductListResponse>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.getPublicProducts(categoryId, pageable)
        );
    }


}
