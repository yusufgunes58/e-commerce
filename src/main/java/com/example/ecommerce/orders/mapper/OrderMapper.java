package com.example.ecommerce.orders.mapper;

import com.example.ecommerce.orders.dto.ShippingAddressDto;
import com.example.ecommerce.orders.dto.response.OrderItemResponse;
import com.example.ecommerce.orders.dto.response.OrderResponse;
import com.example.ecommerce.orders.entity.Order;
import com.example.ecommerce.orders.entity.OrderItem;
import com.example.ecommerce.orders.entity.ShippingAddress;
import com.example.ecommerce.product.dto.internal.OrderProductVariant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderItem toOrderItem(OrderProductVariant productVariant, int quantity);

    OrderResponse toResponse(Order order);

    OrderItemResponse toResponse(OrderItem orderItem);

    ShippingAddress toEntity(ShippingAddressDto request);

    ShippingAddressDto  toResponse(ShippingAddress address);
}
