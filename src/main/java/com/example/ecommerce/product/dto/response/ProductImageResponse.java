package com.example.ecommerce.product.dto.response;

public record ProductImageResponse(
        String imageUrl,
        Integer sortOrder,
        boolean primary
) {}
