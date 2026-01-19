package com.fruit.server.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);

    default Optional<Cart> findActiveCartByUserId(Long userId) {
        return findByUserIdAndStatus(userId, CartStatus.ACTIVE);
    }

    Optional<Cart> findByGuestTokenAndStatus(String guestToken, CartStatus status);

    default Optional<Cart> findActiveCartByGuestToken(String guestToken) {
        return findByGuestTokenAndStatus(guestToken, CartStatus.ACTIVE);
    }
}
