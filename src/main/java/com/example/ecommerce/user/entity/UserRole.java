package com.example.ecommerce.user.entity;

public enum UserRole {
    ADMIN,
    CUSTOMER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
