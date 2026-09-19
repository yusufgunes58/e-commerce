package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.dto.response.customer.ProductListResponse;
import com.example.ecommerce.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // if category has some products, warn it on category page, else default
    long countByCategories_Id(Long categoryId);

    // Customer Get ALL Product List to sorting created_at
    // flyway check unique img primary!
    // if stock=0, write end of list + sorting created_at.
    @Query("""
    SELECT new com.example.ecommerce.product.dto.response.ProductListResponse(
        p.id,
        p.name,
        i.imageUrl,
        COALESCE(
            MIN(
                CASE
                    WHEN v.stockQuantity > 0 THEN v.price
                    ELSE NULL
                END
            ),
            MIN(v.price)
        )
    )
    FROM Product p
    JOIN p.variants v
        ON v.active = true
    LEFT JOIN p.images i
        ON i.isPrimary = true
    WHERE p.active = true
      AND (
          :categoryId IS NULL
          OR EXISTS (
              SELECT c
              FROM p.categories c
              WHERE c.id = :categoryId
          )
      )
    GROUP BY p.id, p.name, i.imageUrl, p.createdAt
    ORDER BY
        CASE
            WHEN EXISTS (
                SELECT v2
                FROM ProductVariant v2
                WHERE v2.product = p
                  AND v2.active = true
                  AND v2.stockQuantity > 0
            )
            THEN 0
            ELSE 1
        END,
        p.createdAt DESC
    """)
    Slice<ProductListResponse> findPublicProducts(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );


   @EntityGraph(attributePaths = "categories")
    @Query("""
        SELECT p
        FROM Product p
        WHERE p.id = :productId
          AND p.active = true
        """)
   Optional<Product> findPublicProductById(
            @Param("productId") Long productId
    );



}
