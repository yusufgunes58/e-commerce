package com.example.ecommerce.cart.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productVariantId,
        String productName,
        String primaryImageUrl,
        String color,
        String size,
        BigDecimal price,
        int quantity
) {
}
