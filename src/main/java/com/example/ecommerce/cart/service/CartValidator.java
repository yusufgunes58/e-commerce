package com.example.ecommerce.cart.service;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.dto.response.FindVariantForCart;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public void validateVariant(FindVariantForCart variant) {
        if (!variant.active()) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_VARIANT_NOT_FOUND
            );
        }
    }

    public void validateStock(
            FindVariantForCart variant,
            int quantity
    ) {
        if (variant.stockQuantity() < quantity) {
            throw new BusinessException(
                    ErrorCode.INSUFFICIENT_STOCK
            );
        }
    }
}
