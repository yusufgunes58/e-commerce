package com.example.ecommerce.product.dto.response.integrationCart;

import java.math.BigDecimal;

public record CartProductVariant(
        Long variantId,
        String productName,
        String primaryImageUrl,
        String color,
        String size,
        BigDecimal price
) {
}