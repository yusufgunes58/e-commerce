package com.example.ecommerce.cart.dto.internal;

public record CartSnapshotItem(
        Long productVariantId,
        Integer quantity
) {
}