package com.example.ecommerce.product.dto.response;

public record FindVariantForCart(
        Long id,
        boolean active,
        int stockQuantity
) {
}