package com.example.ecommerce.product.entity;

import com.example.ecommerce.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//flyway migration cnstraint
//CHECK (stock_quantity >= 0)
//CHECK (price >= 0)
//CHECK (sort_order >= 1)

//PostgreSQL tarafında daha sonra partial unique index ile güvence altına alabiliriz:
//CREATE UNIQUE INDEX uk_product_primary_image
//ON product_image (product_id)
//WHERE is_primary = true;

@Entity
@Table(name = "product_images")
@Getter @Setter @NoArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    @Min(1)
    private Integer sortOrder;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    public ProductImage(
            Product product,
            String imageUrl,
            Integer sortOrder,
            Boolean isPrimary
    ) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.isPrimary = isPrimary;
    }
}
