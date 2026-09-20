package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.ProductVariant;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    @Modifying
    @Query("""
        UPDATE ProductVariant v
        SET v.stockQuantity = v.stockQuantity - :quantity
        WHERE v.id = :variantId
          AND v.stockQuantity >= :quantity
    """)
    int decreaseStock(
            @Param("variantId") Long variantId,
            @Param("quantity") int quantity
    );

    List<ProductVariant> findAllByProductIdAndActiveTrueOrderByIdAsc(
            Long productId
    );

}
