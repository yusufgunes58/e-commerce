package com.example.ecommerce.user.entity;

//import com.example.ecommerce.common.domain.BaseEntity;
import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.common.domain.UserRole;
import com.example.ecommerce.orders.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Address;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User   {
    //   private String gender;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(unique = true,  nullable = false, length = 10)
    private String phone;

    @Column(nullable = false)
    private Boolean active;

    // Role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;

    // Address
    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    //orders
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    //Cart
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;


    public User() {
        this.role = UserRole.CUSTOMER;
        this.active = true;
    }

}
