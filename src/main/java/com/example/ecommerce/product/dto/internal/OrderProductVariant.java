package com.example.ecommerce.product.dto.internal;

import java.math.BigDecimal;

public record OrderProductVariant(
        Long variantId,
        String productName,
        String sku,
        String color,
        String size,
        String productImageUrl,
        BigDecimal unitPrice
) {}