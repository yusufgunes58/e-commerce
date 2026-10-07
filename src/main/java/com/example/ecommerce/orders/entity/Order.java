package com.example.ecommerce.orders.entity;


import com.example.ecommerce.common.domain.BaseEntity;
import com.example.ecommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "contact_mail", nullable = true, length = 20)
    private String contactMail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Embedded
    private ShippingAddress shippingAddress;

    @Column(name = "shipping_company", length = 100)
    private String shippingCompany;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    public Order(
            String orderNumber,
            Long userId,
            String contactPhone,
            BigDecimal totalPrice,
            ShippingAddress shippingAddress
    ) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.contactPhone = contactPhone;
        this.totalPrice = totalPrice;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
    }
}


