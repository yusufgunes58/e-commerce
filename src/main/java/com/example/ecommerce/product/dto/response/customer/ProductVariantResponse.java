package com.example.ecommerce.product.dto.response.customer;

import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        String color,
        String size,
        BigDecimal price,
        Integer stockQuantity
) {}
