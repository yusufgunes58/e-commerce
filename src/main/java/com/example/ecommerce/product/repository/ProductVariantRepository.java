package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.dto.internal.OrderProductVariant;
import com.example.ecommerce.product.dto.response.integrationCart.CartProductVariant;
import com.example.ecommerce.product.entity.ProductVariant;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @Query("""
    SELECT new com.example.ecommerce.product.dto.integration.CartProductVariant(
        v.id,
        p.name,
        i.imageUrl,
        v.color,
        v.size,
        v.price
    )
    FROM ProductVariant v
    JOIN v.product p
    LEFT JOIN p.images i ON i.isPrimary = true
    WHERE v.id IN :variantIds
    """)
    List<CartProductVariant> findCartProductVariants(
            @Param("variantIds") Collection<Long> variantIds
    );

    @Query("""
SELECT new com.example.ecommerce.product.dto.internal.OrderProductVariant(
    v.id,
    p.name,
    v.sku,
    v.color,
    v.size,
    i.imageUrl,
    v.price
)
FROM ProductVariant v
JOIN v.product p
LEFT JOIN p.images i ON i.isPrimary = true
WHERE v.id = :variantId
  AND v.active = true
""")
    Optional<OrderProductVariant> findOrderProductVariant(
            @Param("variantId") Long variantId
    );

    @Modifying
    @Query("""
    UPDATE ProductVariant v
    SET v.stockQuantity = v.stockQuantity - :quantity
    WHERE v.id = :variantId
      AND v.stockQuantity >= :quantity
      AND v.active = true
    """)
    int decreaseStock(
            @Param("variantId") Long variantId,
            @Param("quantity") Integer quantity
    );
}
