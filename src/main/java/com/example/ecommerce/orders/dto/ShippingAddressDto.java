package com.example.ecommerce.orders.dto;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressDto(
        @NotBlank String city,
        @NotBlank String district,
        @NotBlank String neighborhood,
        @NotBlank String fullAddress
) {}
