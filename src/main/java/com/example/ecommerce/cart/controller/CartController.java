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
            @CookieValue(value = "guest_session_id", required = false
            )
            String guestSessionId
    ) {
        return ResponseEntity.ok(
                cartFacadeService.getCart(
                        resolveUserId(userDetails),
                        guestSessionId
                )
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId,
            @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.ok(
                cartFacadeService.addItem(resolveUserId(userDetails), guestSessionId, request)
        );
    }

    @PatchMapping("/items")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId,
            @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(
                cartFacadeService.updateItem(resolveUserId(userDetails), guestSessionId, request)
        );
    }

    @DeleteMapping("/items/{productVariantId}")
    public ResponseEntity<CartResponse> deleteItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guest_session_id", required = false)
            String guestSessionId,
            @PathVariable Long productVariantId
    ) {
        return ResponseEntity.ok(
                cartFacadeService.deleteItem(resolveUserId(userDetails), guestSessionId, productVariantId)
        );
    }

    // helper
    private Long resolveUserId(
            CustomUserDetails userDetails
    ) {
        return userDetails != null
                ? userDetails.getId()
                : null;
    }
}