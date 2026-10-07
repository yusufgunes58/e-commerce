package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.response.CartItemResponse;
import com.example.ecommerce.cart.entity.CartItem;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.dto.response.FindVariantForCart;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartValidatorHelper {

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

    public BigDecimal getTotalPrice(
            List<CartItemResponse> items
    ) {
        return items.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calculateNewQuantity(
            CartItem cartItem,
            int requestedQuantity
    ) {
        if (cartItem == null) {
            return requestedQuantity;
        }
        return cartItem.getQuantity() + requestedQuantity;
    }

}
