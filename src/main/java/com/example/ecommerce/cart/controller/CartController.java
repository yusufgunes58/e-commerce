package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.dto.request.AddCartItemRequest;
import com.example.ecommerce.cart.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.dto.response.CartResponse;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.user.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get Cart by user-id from taking JWT")
    public ResponseEntity<CartResponse> getMyCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    )
    {
        return ResponseEntity.ok( cartService.getMyCart(userDetails.getId())  );
    }

    @PostMapping
    @Operation(summary = "Add product variant to current user's cart")
    public ResponseEntity<Void> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        cartService.addItem(userDetails.getId(), request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<Void> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        cartService.updateItem(
                userDetails.getId(),
                cartItemId,
                request
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Remove item from current user's cart")
    public ResponseEntity<Void> deleteItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId
    ) {
        cartService.deleteItem(
                userDetails.getId(),
                cartItemId
        );

        return ResponseEntity.noContent().build();
    }
}
