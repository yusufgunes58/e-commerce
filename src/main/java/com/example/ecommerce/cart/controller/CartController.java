package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.dto.request.AddCartItemRequest;
import com.example.ecommerce.cart.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.dto.response.CartResponse;
import com.example.ecommerce.cart.service.CartFacadeService;
import com.example.ecommerce.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartFacadeService cartFacadeService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId
    ) {
        return ResponseEntity.ok(
                cartFacadeService.getCart(
                        getUserId(userDetails),
                        guestSessionId
                )
        );
    }

    @PostMapping
    public ResponseEntity<Void> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId,
            @RequestBody AddCartItemRequest request
    ) {
        cartFacadeService.addItem(
                getUserId(userDetails),
                guestSessionId,
                request
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId,
            @RequestBody UpdateCartItemRequest request
    ) {
        cartFacadeService.updateItem(
                getUserId(userDetails),
                guestSessionId,
                request
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{productVariantId}")
    public ResponseEntity<Void> deleteItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId,
            @PathVariable Long productVariantId
    ) {
        cartFacadeService.deleteItem(
                getUserId(userDetails),
                guestSessionId,
                productVariantId
        );

        return ResponseEntity.noContent().build();
    }

    // helper
    private Long getUserId(CustomUserDetails userDetails) {
        return userDetails != null
                ? userDetails.getId()
                : null;
    }
}