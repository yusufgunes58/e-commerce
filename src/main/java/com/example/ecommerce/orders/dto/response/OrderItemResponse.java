package com.example.ecommerce.orders.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productVariantId,
        String productName,
        String sku,
        String color,
        String size,
        String productImageUrl,
        BigDecimal unitPrice,
        Integer quantity
) {}