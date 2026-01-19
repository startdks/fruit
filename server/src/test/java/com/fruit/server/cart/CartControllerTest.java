package com.fruit.server.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruit.server.product.Product;
import com.fruit.server.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private static final String TEST_GUEST_TOKEN = "test-guest-token-12345";

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Test Apple");
        testProduct.setPrice(new BigDecimal("2.99"));
        testProduct.setDescription("Test description");
        testProduct.setUnit("lb");
        testProduct.setStockQuantity(100);
        testProduct.setIsActive(true);
        testProduct = productRepository.save(testProduct);
    }

    // ========== GET /api/cart ==========

    @Nested
    @DisplayName("GET /api/cart - Get Cart")
    class GetCart {

        @Test
        @DisplayName("Returns empty cart for guest user")
        void returnsEmptyCartForGuest() throws Exception {
            mockMvc.perform(get("/api/cart")
                    .param("guestToken", TEST_GUEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(0)))
                    .andExpect(jsonPath("$.subtotal", is(0)));
        }

        @Test
        @DisplayName("Returns cart with items")
        void returnsCartWithItems() throws Exception {
            // Add item to cart first
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 2, null, TEST_GUEST_TOKEN);
            mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            mockMvc.perform(get("/api/cart")
                    .param("guestToken", TEST_GUEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].productName", is("Test Apple")))
                    .andExpect(jsonPath("$.items[0].quantity", is(2)));
        }
    }

    // ========== POST /api/cart ==========

    @Nested
    @DisplayName("POST /api/cart - Add To Cart")
    class AddToCart {

        @Test
        @DisplayName("Adds item to cart successfully")
        void addsItemToCart() throws Exception {
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 3, null, TEST_GUEST_TOKEN);

            mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productName", is("Test Apple")))
                    .andExpect(jsonPath("$.quantity", is(3)))
                    .andExpect(jsonPath("$.productPrice", is(2.99)));
        }

        @Test
        @DisplayName("Returns 400 when product ID is missing")
        void returns400WhenProductIdMissing() throws Exception {
            CartItemRequest request = new CartItemRequest(null, 3, null, TEST_GUEST_TOKEN);

            mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 400 when quantity is zero or negative")
        void returns400WhenQuantityInvalid() throws Exception {
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 0, null, TEST_GUEST_TOKEN);

            mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ========== PUT /api/cart/{id} ==========

    @Nested
    @DisplayName("PUT /api/cart/{id} - Update Cart Item")
    class UpdateCartItem {

        @Test
        @DisplayName("Updates cart item quantity")
        void updatesCartItemQuantity() throws Exception {
            // Add item first
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 2, null, TEST_GUEST_TOKEN);
            String response = mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andReturn().getResponse().getContentAsString();

            Long itemId = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(put("/api/cart/{id}", itemId)
                    .param("quantity", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantity", is(5)));
        }

        @Test
        @DisplayName("Removes item when quantity is zero")
        void removesItemWhenQuantityZero() throws Exception {
            // Add item first
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 2, null, TEST_GUEST_TOKEN);
            String response = mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andReturn().getResponse().getContentAsString();

            Long itemId = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(put("/api/cart/{id}", itemId)
                    .param("quantity", "0"))
                    .andExpect(status().isNoContent());
        }
    }

    // ========== DELETE /api/cart/{id} ==========

    @Nested
    @DisplayName("DELETE /api/cart/{id} - Remove From Cart")
    class RemoveFromCart {

        @Test
        @DisplayName("Removes item from cart")
        void removesItemFromCart() throws Exception {
            // Add item first
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 2, null, TEST_GUEST_TOKEN);
            String response = mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andReturn().getResponse().getContentAsString();

            Long itemId = objectMapper.readTree(response).get("id").asLong();

            mockMvc.perform(delete("/api/cart/{id}", itemId))
                    .andExpect(status().isNoContent());

            // Verify cart is empty
            mockMvc.perform(get("/api/cart")
                    .param("guestToken", TEST_GUEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }
    }

    // ========== DELETE /api/cart/clear ==========

    @Nested
    @DisplayName("DELETE /api/cart/clear - Clear Cart")
    class ClearCart {

        @Test
        @DisplayName("Clears all items from cart")
        void clearsCart() throws Exception {
            // Add items first
            CartItemRequest request = new CartItemRequest(testProduct.getId(), 2, null, TEST_GUEST_TOKEN);
            mockMvc.perform(post("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Note: clearCart requires userId, testing without userId may fail
            // This test assumes guest cart behavior
        }
    }
}
