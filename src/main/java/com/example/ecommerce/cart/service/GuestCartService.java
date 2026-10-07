package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.request.AddCartItemRequest;
import com.example.ecommerce.cart.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.dto.response.CartItemResponse;
import com.example.ecommerce.cart.dto.response.CartResponse;

import com.example.ecommerce.cart.repository.GuestCartRepository;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.dto.response.FindVariantForCart;
import com.example.ecommerce.product.dto.response.integrationCart.CartProductVariant;
import com.example.ecommerce.product.service.ProductVariantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestCartService {

    private final GuestCartRepository guestCartRepository;
    private final ProductVariantService productVariantService;
    private final CartValidator cartValidator;

    public CartResponse addItem(String sessionId,
                        AddCartItemRequest request
    ) {
        log.debug(
                "Adding item to guest cart. sessionId={}, productVariantId={}, quantity={}",
                sessionId,
                request.productVariantId(),
                request.quantity()
        );

        FindVariantForCart variant =
                productVariantService.getVariantInfo(request.productVariantId());

        cartValidator.validateVariant(variant);

        Map<Long, Integer> items = guestCartRepository.find(sessionId);

        int currentQuantity =
                items.getOrDefault(request.productVariantId(), 0);

        int newQuantity =
                currentQuantity + request.quantity();

        cartValidator.validateStock(variant, newQuantity);

        guestCartRepository.addItem(
                sessionId,
                variant.id(),
                request.quantity()
        );

        log.info("Guest cart item added. sessionId={}, productVariantId={}, quantity={}",
                sessionId,
                variant.id(),
                newQuantity
        );

        return getCart(sessionId);
    }


    public CartResponse getCart(String sessionId) {

        log.debug("Fetching guest cart. sessionId={}", sessionId);

        Map<Long, Integer> items = guestCartRepository.find(sessionId);

        if (items == null || items.isEmpty()) {
            return new CartResponse(
                    List.of(),
                    BigDecimal.ZERO
            );
        }

        List<Long> variantIds = items.keySet()
                .stream()
                .toList();

        Map<Long, CartProductVariant> productVariants =
                productVariantService.findCartProductVariants(variantIds);

        List<CartItemResponse> cartItems = items.entrySet()
                .stream()
                .map(entry -> {

                    Long productVariantId = entry.getKey();
                    Integer quantity = entry.getValue();

                    CartProductVariant productVariant =
                            productVariants.get(productVariantId);

                    BigDecimal totalPrice =
                            productVariant.price()
                                    .multiply(
                                            BigDecimal.valueOf(quantity)
                                    );

                    return new CartItemResponse(
                            productVariantId,
                            productVariant.productName(),
                            productVariant.primaryImageUrl(),
                            productVariant.color(),
                            productVariant.size(),
                            productVariant.price(),
                            quantity,
                            totalPrice
                    );
                })
                .toList();

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Fetched guest cart. sessionId={}, itemCount={}", sessionId, cartItems.size());

        return new CartResponse(cartItems, totalPrice);
    }

    public CartResponse updateItem(String sessionId, UpdateCartItemRequest request) {
        Long productVariantId = request.productVariantId();

        log.debug("Updating guest cart item. sessionId={}, productVariantId={}, quantity={}",
                sessionId, productVariantId, request.quantity());

        Map<Long, Integer> items = guestCartRepository.find(sessionId);

        if (!items.containsKey(productVariantId)) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        FindVariantForCart variant = productVariantService.getVariantInfo(productVariantId);

        cartValidator.validateVariant(variant);
        cartValidator.validateStock(variant, request.quantity());


        guestCartRepository.updateItem(
                sessionId,
                productVariantId,
                request.quantity()
        );

        log.info(
                "Guest cart item updated. sessionId={}, productVariantId={}, quantity={}",
                sessionId,
                productVariantId,
                request.quantity()
        );

        return getCart(sessionId);
    }

    public CartResponse deleteItem(String sessionId, Long productVariantId) {
        log.debug("Deleting guest cart item. sessionId={}, productVariantId={}",
                sessionId,
                productVariantId
        );

        Map<Long, Integer> items = guestCartRepository.find(sessionId);

        if (!items.containsKey(productVariantId)) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        guestCartRepository.removeItem(sessionId, productVariantId);

        log.info("Guest cart item deleted. sessionId={}, productVariantId={}",
                sessionId,
                productVariantId
        );

        return getCart(sessionId);
    }

}