package com.example.ecommerce.cart.repository;

import com.example.ecommerce.cart.entity.CartItem;
import com.example.ecommerce.cart.repository.view.CartItemSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductVariantId(
            Long cartId,
            Long productVariantId
    );

/*
    @Query("""
            SELECT new com.example.ecommerce.cart.dto.response.CartItemResponse(
                ci.id,
                v.id,
                p.name,
                i.imageUrl,
                v.color,
                v.size,
                v.price,
                ci.quantity
            )
            FROM CartItem ci
            JOIN ci.cart c
            JOIN ci.productVariant v
            JOIN v.product p
            LEFT JOIN p.images i ON i.isPrimary = true
            WHERE c.id = :cartId
            ORDER BY ci.id
            """)
    List<CartItemResponse> findCartItems(@Param("cartId") Long cartId   );
*/

    @Query("""
        SELECT
            ci.id AS id,
            ci.productVariantId AS productVariantId,
            ci.quantity AS quantity
        FROM CartItem ci
        WHERE ci.cart.id = :cartId
        ORDER BY ci.id
        """)
    List<CartItemSummary> findCartItemSummaries(
            @Param("cartId") Long cartId
    );


    Optional<CartItem> findByIdAndCartUserId(
            Long cartItemId,
            Long userId
    );


}
