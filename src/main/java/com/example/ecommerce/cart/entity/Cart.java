package com.example.ecommerce.cart.entity;

import com.example.ecommerce.common.domain.BaseEntity;
import com.example.ecommerce.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
public class Cart extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
