package com.example.ecommerce.orders.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ShippingAddress {

    private String city;

    private String district;

    private String neighborhood;

    private String fullAddress;
}
