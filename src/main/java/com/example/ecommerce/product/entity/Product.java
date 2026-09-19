package com.example.ecommerce.product.entity;

import com.example.ecommerce.category.entity.Category;
import com.example.ecommerce.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor
public class Product extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "brand_id", nullable = false)
    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductImage> images = new ArrayList<>();

    public Product(
            String name,
            String description,
            Set<Category> categories ,
            String brand
    ) {
        this.name = name;
        this.description = description;
        this.categories = categories != null
                ? new HashSet<>(categories)
                : new HashSet<>();
        this.brand = brand;
        this.active = true;
    }

}
