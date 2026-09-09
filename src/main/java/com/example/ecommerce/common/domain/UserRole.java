package com.example.ecommerce.common.domain;

public enum UserRole {
    ADMIN,
    CUSTOMER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
