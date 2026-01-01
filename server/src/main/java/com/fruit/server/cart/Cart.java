package com.fruit.server.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fruit.server.user.User;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carts")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}