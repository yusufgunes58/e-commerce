package com.example.ecommerce.orders.service;


import com.example.ecommerce.cart.dto.internal.CartSnapshot;
import com.example.ecommerce.cart.dto.internal.CartSnapshotItem;
import com.example.ecommerce.cart.service.CartFacadeService;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.orders.dto.request.CreateOrderRequest;
import com.example.ecommerce.orders.dto.response.OrderResponse;
import com.example.ecommerce.orders.entity.Order;
import com.example.ecommerce.orders.entity.OrderItem;
import com.example.ecommerce.orders.entity.ShippingAddress;
import com.example.ecommerce.orders.mapper.OrderMapper;
import com.example.ecommerce.orders.repository.OrderRepository;
import com.example.ecommerce.product.dto.internal.OrderProductVariant;
import com.example.ecommerce.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartFacadeService cartFacadeService;
    private final ProductVariantService productVariantService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(
            Long userId,
            String sessionId,
            CreateOrderRequest request
    ) {
        CartSnapshot cart = cartFacadeService.getCartSnapshot(userId, sessionId);

        if (cart.items().isEmpty()) {
            throw new BusinessException(ErrorCode.CART_NOT_FOUND);
        }

        ShippingAddress address =
                orderMapper.toEntity(request.shippingAddress());

        Order order = new Order(
                generateOrderNumber(),
                userId,
                request.contactPhone(),
                BigDecimal.ZERO,
                address
        );

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartSnapshotItem cartItem : cart.items()) {

            OrderProductVariant productVariant =
                    productVariantService.getVariantForOrder(
                            cartItem.productVariantId()
                    );

            productVariantService.decreaseStock(
                    cartItem.productVariantId(),
                    cartItem.quantity()
            );

            OrderItem orderItem = orderMapper.toOrderItem(
                    productVariant,
                    cartItem.quantity()
            );

            orderItem.setOrder(order);
            order.getItems().add(orderItem);

            totalPrice = totalPrice.add(
                    productVariant.unitPrice()
                            .multiply(
                                    BigDecimal.valueOf(cartItem.quantity())
                            )
            );
        }

        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        log.info(
                "Order created: orderNumber={}, itemCount={}, totalPrice={}",
                savedOrder.getOrderNumber(),
                savedOrder.getItems().size(),
                savedOrder.getTotalPrice()
        );

        return orderMapper.toResponse(savedOrder);
    }





    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID();
    }


}
