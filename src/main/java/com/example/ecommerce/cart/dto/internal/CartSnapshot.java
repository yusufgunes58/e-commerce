package com.example.ecommerce.cart.dto.internal;
import java.util.List;

public record CartSnapshot(
        List<CartSnapshotItem> items
) {
}