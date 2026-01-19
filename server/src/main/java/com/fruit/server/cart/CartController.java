package com.fruit.server.cart;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam(required = false) Long userId,
            @RequestParam(required = false) String guestToken) {
        CartResponse cartResponse = cartService.getCartResponse(userId, guestToken);
        return ResponseEntity.ok(cartResponse);
    }

    @GetMapping("/guest-token")
    public ResponseEntity<String> generateGuesetToken() {
        String token = UUID.randomUUID().toString();
        return ResponseEntity.ok(token);
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            CartItemResponse cartItem = cartService.addToCartAndGetResponse(
                    request.userId(),
                    request.productId(),
                    request.quantity(),
                    request.guestToken());
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        try {
            CartItemResponse updatedItem = cartService.updateQuantityAndGetResponse(cartItemId, quantity);
            if (updatedItem == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        try {
            cartService.removeFromCart(cartItemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferGuestCart(@RequestParam Long userId, @RequestParam String guestToken) {
        cartService.transferGuestCartToUser(userId, guestToken);
        return ResponseEntity.ok().build();
    }
}