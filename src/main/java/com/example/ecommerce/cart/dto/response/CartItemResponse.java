package com.example.ecommerce.cart.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productVariantId,
        String productName,
        String primaryImageUrl,
        String color,
        String size,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal totalPrice
) {
}
