package com.example.ecommerce.cart.listener;

import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.user.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final CartService cartService;

    @EventListener
    public void handle(@NonNull UserRegisteredEvent event) {
        cartService.createCart(event.userId());
    }
}
