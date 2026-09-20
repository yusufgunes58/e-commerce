package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // db migration check primary img

    List<ProductImage> findAllByProductIdOrderBySortOrderAsc(
            Long productId
    );
}
