package com.example.ecommerce.category.dto.response;

public record CategoryDeleteInfoResponse(
        Long categoryId,
        String categoryName,
        long productCount
) {}
