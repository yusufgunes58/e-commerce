package com.example.ecommerce.product.dto.response.customer;

import java.math.BigDecimal;

public record ProductListResponse(
        Long id,
        String name,
        String primaryImageUrl,
        BigDecimal minPrice
) {
}
