package com.fruit.server.cart;

import com.fruit.server.product.Product;
import com.fruit.server.product.ProductService;
import com.fruit.server.user.User;
import com.fruit.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.fruit.server.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    // Public API methods for Controller - returns DTOs

    // Get cart for user
    public CartResponse getCartResponse(Long userId) {
        List<CartItem> items = getCartItems(userId);
        return CartResponse.from(items);
    }

    // Add product to cart
    @Transactional
    public CartItemResponse addToCartAndGetResponse(Long userId, Long productId, Integer quantity) {
        CartItem item = addToCart(userId, productId, quantity);
        return CartItemResponse.from(item);
    }

    // Update item quantity
    @Transactional
    public CartItemResponse updateQuantityAndGetResponse(Long cartItemId, Integer quantity) {
        CartItem item = updateQuantity(cartItemId, quantity);
        return item != null ? CartItemResponse.from(item) : null;
    }

    // Remove item from cart
    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // Transfer guest cart to user cart
    @Transactional
    public void transferGuestCartToUser(Long userId) {
        var guestCartOpt = cartRepository.findByUserIsNullAndStatus(CartStatus.ACTIVE);
        if (guestCartOpt.isEmpty()) {
            return;
        }

        Cart guestCart = guestCartOpt.get();
        List<CartItem> guestItems = cartItemRepository.findByCartId(guestCart.getId());

        if (guestItems.isEmpty()) {
            return;
        }

        Cart userCart = getOrCreateActiveCart(userId);

        for (CartItem guestItem : guestItems) {
            var existingItem = cartItemRepository.findByCartIdAndProductId(
                    userCart.getId(), guestItem.getProduct().getId());

            if (existingItem.isPresent()) {
                CartItem userItem = existingItem.get();
                userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
                cartItemRepository.save(userItem);
            } else {
                guestItem.setCart(userCart);
                cartItemRepository.save(guestItem);
            }
        }

        guestCart.setStatus(CartStatus.ABANDONED);
        cartRepository.save(guestCart);
    }

    // Internal methods for other services - returns Entity

    // Clear all items in cart
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    // Mark cart as converted to order
    @Transactional
    public void convertCartToOrder(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        cart.setStatus(CartStatus.CONVERTED_TO_ORDER);
        cartRepository.save(cart);
    }

    // Get active cart entity
    public Cart getActiveCart(Long userId) {
        return getOrCreateActiveCart(userId);
    }

    // Private helper methods

    // Get or create active cart for user
    @Transactional
    Cart getOrCreateActiveCart(Long userId) {
        if (userId != null) {
            return cartRepository.findActiveCartByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));
        } else {
            return cartRepository.findByUserIsNullAndStatus(CartStatus.ACTIVE)
                    .orElseGet(() -> createNewCart(null));
        }
    }

    // Create new cart
    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        if (userId != null) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            cart.setUser(user);
        }
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    // Get cart items by user
    List<CartItem> getCartItems(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        return cartItemRepository.findByCartId(cart.getId());
    }

    // Add product to cart (internal)
    @Transactional
    CartItem addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateActiveCart(userId);
        Product product = productService.findProductById(productId);

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPriceAtAddition(product.getPrice());
            return cartItemRepository.save(newItem);
        }
    }

    // Update quantity (internal)
    @Transactional
    CartItem updateQuantity(Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", cartItemId));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }
}
