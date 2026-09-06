package com.example.ecommerce.common.domain;

public enum Role {
    ADMIN,
    CUSTOMER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
