package com.example.ecommerce.orders.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false, length = 100)
    private String color;

    @Column(nullable = false, length = 100)
    private String size;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    public OrderItem(
            Order order,
            Long productVariantId,
            String productName,
            String sku,
            String color,
            String size,
            String productImageUrl,
            BigDecimal unitPrice,
            Integer quantity
    ) {
        this.order = order;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.sku = sku;
        this.color = color;
        this.size = size;
        this.productImageUrl = productImageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
}

