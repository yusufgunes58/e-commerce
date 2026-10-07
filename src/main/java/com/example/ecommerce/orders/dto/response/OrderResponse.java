package com.example.ecommerce.orders.dto.response;

import com.example.ecommerce.orders.dto.ShippingAddressDto;
import com.example.ecommerce.orders.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        String orderNumber,
        OrderStatus status,
        BigDecimal totalPrice,
        ShippingAddressDto shippingAddress,
        List<OrderItemResponse> items
) {}
