package com.example.ecommerce.product.entity;

import com.example.ecommerce.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String barcode;

    @Column(nullable = false, length = 100)
    private String color;

    @Column(nullable = false, length = 100)
    private String size;

    @Column(nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "stock_quantity", nullable = false)
    @Min(0)
    private Integer stockQuantity = 0;

    public ProductVariant(
            Product product,
            String barcode,
            String color,
            String size,
            BigDecimal price,
            Integer stockQuantity
    ) {
        this.product = product;
        this.barcode = barcode;
        this.color = color;
        this.size = size;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = true;
    }


}
