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
    public CartResponse getCartResponse(Long userId, String guestToken) {
        List<CartItem> items = getCartItems(userId, guestToken);
        return CartResponse.from(items);
    }

    // Add product to cart
    @Transactional
    public CartItemResponse addToCartAndGetResponse(Long userId, Long productId, Integer quantity, String guestToken) {
        CartItem item = addToCart(userId, productId, quantity, guestToken);
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
    public void transferGuestCartToUser(Long userId, String guestToken) {
        Optional<Cart> guestCartOpt = cartRepository.findActiveCartByGuestToken(guestToken);
        if (guestCartOpt.isEmpty()) {
            return;
        }

        Cart guestCart = guestCartOpt.get();
        List<CartItem> guestItems = cartItemRepository.findByCartId(guestCart.getId());

        if (guestItems.isEmpty()) {
            return;
        }

        Cart userCart = getOrCreateActiveCart(userId, null);

        for (CartItem guestItem : guestItems) {
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
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
        Cart cart = getOrCreateActiveCart(userId, null);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    // Mark cart as converted to order
    @Transactional
    public void convertCartToOrder(Long userId) {
        Cart cart = getOrCreateActiveCart(userId, null);
        cart.setStatus(CartStatus.CONVERTED_TO_ORDER);
        cartRepository.save(cart);
    }

    // Get active cart entity
    public Cart getActiveCart(Long userId) {
        return getOrCreateActiveCart(userId, null);
    }

    // Private helper methods

    // Get or create active cart for user
    @Transactional
    Cart getOrCreateActiveCart(Long userId, String guestToken) {
        if (userId != null) {
            return cartRepository.findActiveCartByUserId(userId)
                    .orElseGet(() -> createNewCart(userId, null));
        } else if (guestToken != null) {
            return cartRepository.findActiveCartByGuestToken(guestToken)
                    .orElseGet(() -> createNewCart(null, guestToken));
        } else {
            throw new IllegalArgumentException("Either userId or guestToken must be provided");
        }
    }

    // Create new cart
    private Cart createNewCart(Long userId, String guestToken) {
        Cart cart = new Cart();
        if (userId != null) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            cart.setUser(user);
        }
        cart.setGuestToken(guestToken);
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    // Get cart items by user
    List<CartItem> getCartItems(Long userId, String guestToken) {
        Cart cart = getOrCreateActiveCart(userId, guestToken);
        return cartItemRepository.findByCartId(cart.getId());
    }

    // Add product to cart (internal)
    @Transactional
    CartItem addToCart(Long userId, Long productId, Integer quantity, String guestToken) {
        Cart cart = getOrCreateActiveCart(userId, guestToken);
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
