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
                            cartItem.getId(),
                            cartItem.getProductVariantId(),
                            productVariant.productName(),
                            productVariant.primaryImageUrl(),
                            productVariant.color(),
                            productVariant.size(),
                            productVariant.price(),
                            cartItem.getQuantity()
                    );
                })
                .toList();

        BigDecimal totalPrice = getTotalPrice(items);

        log.debug("Fetched cart. userId={}, itemCount={}",
                userId,
                items.size()     );

        return new CartResponse(items, totalPrice);
    }

    @Transactional
    public void addItem(Long userId, @NonNull AddCartItemRequest request) {

        log.debug("Adding item to cart. userId={}, productVariantId={}, quantity={}",
                userId,
                request.productVariantId(),
                request.quantity()
        );

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_NOT_FOUND)  );

        FindVariantForCart variant =
                productVariantService.getVariantInfo(
                        request.productVariantId()     );

        validateVariant(variant);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductVariantId(
                        cart.getId(),
                        variant.id()
                )
                .orElse(null);

        int newQuantity = calculateNewQuantity( cartItem, request.quantity()  );

        validateStock(variant, newQuantity);

        if (cartItem != null) {
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem(
                    cart,
                    variant.id(),
                    newQuantity      );
            cartItemRepository.save(cartItem);
        }

        log.info("Cart item added. userId={}, productVariantId={}, quantity={}",
                userId,
                variant.id(),
                newQuantity    );

    }

    @Transactional
    public void updateItem(
            Long userId,
            Long cartItemId,
            @NonNull UpdateCartItemRequest request
    ) {

        log.debug("Updating cart item. userId={}, cartItemId={}, quantity={}",
                userId,
                cartItemId,
                request.quantity()
        );

        CartItem cartItem = cartItemRepository
                .findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)
                );

        FindVariantForCart variant =
                productVariantService.getVariantInfo(
                        cartItem.getProductVariantId()
                );

        validateVariant(variant);
        validateStock(variant, request.quantity());

        cartItem.setQuantity(request.quantity());

        log.info(
                "Cart item updated. userId={}, cartItemId={}, quantity={}",
                userId,
                cartItemId,
                request.quantity()
        );
    }


    @Transactional
    public void deleteItem(Long userId, Long cartItemId) {

        log.debug("Deleting cart item. userId={}, cartItemId={}",
                userId,
                cartItemId
        );

        CartItem cartItem = cartItemRepository
                .findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)
                );

        cartItemRepository.delete(cartItem);

        log.info("Cart item deleted. userId={}, cartItemId={}, productVariantId={}",
                userId,
                cartItemId,
                cartItem.getProductVariantId()
        );
    }

    // HELPERS

    private BigDecimal getTotalPrice(@NonNull List<CartItemResponse> items) {
        return items.stream()
                .map(item ->
                        item.price()
                                .multiply(BigDecimal.valueOf(item.quantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateVariant(FindVariantForCart variant) {

        if (!variant.active()) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_VARIANT_NOT_FOUND
            );
        }
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

    private void validateStock(
            FindVariantForCart variant,
            int quantity
    ) {
        if (variant.stockQuantity() < quantity) {
            throw new BusinessException(
                    ErrorCode.INSUFFICIENT_STOCK
            );
        }
    }
}
