package com.example.ecommerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {

    // Auth
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "Your account is not active"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission for this action"),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "Session expired, please log in again"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "This email address is already registered"),

    // General
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Category name already exists"),
    CATEGORY_DELETE_CONFIRMATION_REQUIRED(HttpStatus.CONFLICT, "Category deletion requires confirmation"),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
    PRODUCT_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Product name already exists"),
    PRODUCT_VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product variant not found"),
    PRODUCT_SKU_ALREADY_EXISTS(HttpStatus.CONFLICT, "Product SKU already exists"),
    PRODUCT_BARCODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Product barcode already exists"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Insufficient stock"),

    // Cart + CartItem
    CART_NOT_FOUND(HttpStatus.NOT_FOUND,"Cart not found");








    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
