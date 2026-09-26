package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.request.AddCartItemRequest;
import com.example.ecommerce.cart.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.dto.response.CartItemResponse;
import com.example.ecommerce.cart.dto.response.CartResponse;
import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.entity.CartItem;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.cart.repository.view.CartItemSummary;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;

import com.example.ecommerce.product.dto.response.FindVariantForCart;
import com.example.ecommerce.product.dto.response.integrationCart.CartProductVariant;
import com.example.ecommerce.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantService productVariantService;
    private final CartValidator cartValidator;

    @Transactional
    public Cart createCart(Long userId) {
        Cart cart = new Cart(userId);
        Cart savedCart = cartRepository.save(cart);

        log.info("Cart created. cartId={}, userId={}",
                savedCart.getId(),
                userId
        );
        return savedCart;
    }


    public CartResponse getMyCart(Long userId) {

        log.debug("Fetching cart. userId={}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_NOT_FOUND)
                );

        List<CartItemSummary> cartItems =
                cartItemRepository.findCartItemSummaries(cart.getId());

        if (cartItems.isEmpty()) {
            return new CartResponse(
                    List.of(),
                    BigDecimal.ZERO
            );
        }

        List<Long> variantIds = cartItems.stream()
                .map(CartItemSummary::getProductVariantId)
                .toList();

        Map<Long, CartProductVariant> productVariants =
                productVariantService.findCartProductVariants(variantIds);

        List<CartItemResponse> items = cartItems.stream()
                .map(cartItem -> {

                    CartProductVariant productVariant =
                            productVariants.get(
                                    cartItem.getProductVariantId()
                            );

                    return new CartItemResponse(
                            cartItem.getProductVariantId(),
                            productVariant.productName(),
                            productVariant.primaryImageUrl(),
                            productVariant.color(),
                            productVariant.size(),
                            productVariant.price(),
                            cartItem.getQuantity(),
                            productVariant.price()
                                    .multiply(
                                            BigDecimal.valueOf(cartItem.getQuantity())
                                    )
                    );
                })
                .toList();

        BigDecimal totalPrice = getTotalPrice(items);

        log.debug("Fetched cart. userId={}, itemCount={}",
                userId,
                items.size());

        return new CartResponse(items, totalPrice);
    }

    @Transactional
    public void addItem(
            Long userId,
            @NonNull AddCartItemRequest request
    ) {

        log.debug("Adding item to cart. userId={}, productVariantId={}, quantity={}",
                userId,
                request.productVariantId(),
                request.quantity()
        );

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_NOT_FOUND)
                );

        FindVariantForCart variant =
                productVariantService.getVariantInfo(
                        request.productVariantId()
                );

        cartValidator.validateVariant(variant);

        CartItem cartItem = cartItemRepository
                .findByCartUserIdAndProductVariantId(
                        userId,
                        variant.id()
                )
                .orElse(null);

        int newQuantity = calculateNewQuantity(
                cartItem,
                request.quantity()
        );

        cartValidator.validateStock(
                variant,
                newQuantity
        );

        if (cartItem != null) {
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem(
                    cart,
                    variant.id(),
                    newQuantity
            );

            cartItemRepository.save(cartItem);
        }

        log.info("Cart item added. userId={}, productVariantId={}, quantity={}",
                userId,
                variant.id(),
                newQuantity
        );
    }

    @Transactional
    public void updateItem(
            Long userId,
            @NonNull UpdateCartItemRequest request
    ) {
        log.debug(
                "Updating cart item. userId={}, productVariantId(),={}, quantity={}",
                userId,
                request.productVariantId(),
                request.quantity()
        );

        CartItem cartItem = cartItemRepository
                .findByCartUserIdAndProductVariantId(
                        userId,
                        request.productVariantId()
                )
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)
                );

        FindVariantForCart variant =
                productVariantService.getVariantInfo(request.productVariantId());

        cartValidator.validateVariant(variant);
        cartValidator.validateStock(variant, request.quantity());

        cartItem.setQuantity(request.quantity());

        log.info(
                "Cart item updated. userId={}, productVariantId={}, quantity={}",
                userId,
                request.productVariantId(),
                request.quantity()
        );
    }


    @Transactional
    public void deleteItem(
            Long userId,
            Long productVariantId
    ) {
        log.debug(
                "Deleting cart item. userId={}, productVariantId={}",
                userId,
                productVariantId
        );

        CartItem cartItem = cartItemRepository
                .findByCartUserIdAndProductVariantId(
                        userId,
                        productVariantId
                )
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)
                );

        cartItemRepository.delete(cartItem);

        log.info(
                "Cart item deleted. userId={}, productVariantId={}",
                userId,
                productVariantId
        );
    }


    // HELPERS

    private BigDecimal getTotalPrice(
            List<CartItemResponse> items
    ) {
        return items.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int calculateNewQuantity(
            CartItem cartItem,
            int requestedQuantity
    ) {
        if (cartItem == null) {
            return requestedQuantity;
        }
        return cartItem.getQuantity() + requestedQuantity;
    }

}
