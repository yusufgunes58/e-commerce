package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.request.AddCartItemRequest;
import com.example.ecommerce.cart.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartFacadeService {

    private final CartService cartService;
    private final GuestCartService guestCartService;


    public CartResponse getCart(Long userId, String sessionId) {
        if(userId!=null) {
            return cartService.getCart(userId);
        }
        return guestCartService.getCart(sessionId);
    }

    public void addItem(
            Long userId,
            String sessionId,
            AddCartItemRequest request
    ) {
        if (userId != null) {
            cartService.addItem(userId, request);
            return;
        }

        guestCartService.addItem(sessionId, request);
    }


    public void updateItem(
            Long userId,
            String sessionId,
            UpdateCartItemRequest request
    ) {
        if (userId != null) {
            cartService.updateItem(
                    userId,
                    request
            );
            return;
        }

        guestCartService.updateItem(
                sessionId,
                request
        );
    }

    public void deleteItem(
            Long userId,
            String sessionId,
            Long productVariantId
    ) {
        if (userId != null) {
            cartService.deleteItem(
                    userId,
                    productVariantId
            );
            return;
        }

        guestCartService.deleteItem(
                sessionId,
                productVariantId
        );
    }
}