package com.example.ecommerce.product.dto.response.customer;

import com.example.ecommerce.product.dto.response.ProductImageResponse;

import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        List<String> categories,
        List<ProductVariantResponse> variants,
        List<ProductImageResponse> images
) {}
