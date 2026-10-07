package com.example.ecommerce.orders.dto.request;

import com.example.ecommerce.orders.dto.ShippingAddressDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotBlank
        String contactPhone,

        @NotNull
        ShippingAddressDto shippingAddress
) {}
