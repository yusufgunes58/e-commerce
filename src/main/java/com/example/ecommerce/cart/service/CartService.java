package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.response.CartItemResponse;
import com.example.ecommerce.cart.dto.response.CartResponse;
import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Cart createCart(Long userId) {
        Cart cart = new Cart(userId);
        log.info(
                "Cart created. cartId={}, userId={}",
                cart.getId(),
                userId
        );
        return cartRepository.save(cart);
    }

    public CartResponse getMyCart(Long userId) {

        log.debug("Fetching cart. userId={}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_NOT_FOUND)       );

        List<CartItemResponse> items = cartItemRepository.findCartItems(cart.getId());

        BigDecimal totalPrice = getTotalPrice(items);

        log.debug("Fetched cart. userId={}, itemCount={}", userId,  items.size()     );

        return new CartResponse(items, totalPrice);
    }

    @Transactional



    // helper
    private BigDecimal getTotalPrice(List<CartItemResponse> items) {
        return items.stream()
                .map(item ->
                        item.price()
                                .multiply(BigDecimal.valueOf(item.quantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
