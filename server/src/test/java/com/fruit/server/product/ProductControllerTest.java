package com.fruit.server.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    // ========== Helper Methods ==========

    private ProductRequest createValidProductRequest() {
        return new ProductRequest(
                "Fresh Apple",
                "Delicious red apple",
                new BigDecimal("2.99"),
                "https://example.com/apple.jpg",
                "lb",
                100,
                "USA",
                true);
    }

    private Product createAndSaveProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription("Test description");
        product.setUnit("lb");
        product.setStockQuantity(50);
        product.setIsActive(true);
        return productRepository.save(product);
    }

    // ========== GET /api/products ==========

    @Nested
    @DisplayName("GET /api/products - Get All Products")
    class GetAllProducts {

        @Test
        @DisplayName("Returns empty list when no products exist")
        void returnsEmptyListWhenNoProducts() throws Exception {
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Returns product list successfully")
        void returnsProductList() throws Exception {
            createAndSaveProduct("Apple", new BigDecimal("2.99"));
            createAndSaveProduct("Banana", new BigDecimal("1.49"));

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").exists())
                    .andExpect(jsonPath("$[1].name").exists());
        }

        @Test
        @DisplayName("Returns only active products")
        void returnsOnlyActiveProducts() throws Exception {
            Product active = createAndSaveProduct("Active Product", new BigDecimal("1.00"));
            Product inactive = createAndSaveProduct("Inactive Product", new BigDecimal("2.00"));
            inactive.setIsActive(false);
            productRepository.save(inactive);

            mockMvc.perform(get("/api/products").param("isActive", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("Active Product")));
        }
    }

    // ========== GET /api/products/{id} ==========

    @Nested
    @DisplayName("GET /api/products/{id} - Get Product By ID")
    class GetProductById {

        @Test
        @DisplayName("Returns product when it exists")
        void returnsProductWhenExists() throws Exception {
            Product product = createAndSaveProduct("Apple", new BigDecimal("2.99"));

            mockMvc.perform(get("/api/products/{id}", product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                    .andExpect(jsonPath("$.name", is("Apple")))
                    .andExpect(jsonPath("$.price", is(2.99)));
        }

        @Test
        @DisplayName("Returns 404 when product not found")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/products/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    // ========== POST /api/products ==========

    @Nested
    @DisplayName("POST /api/products - Create Product")
    class CreateProduct {

        @Test
        @DisplayName("Creates product with valid request")
        @WithMockUser
        void createsProductWithValidRequest() throws Exception {
            ProductRequest request = createValidProductRequest();

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name", is("Fresh Apple")))
                    .andExpect(jsonPath("$.price", is(2.99)));
        }

        @Test
        @DisplayName("Returns 400 when name is missing")
        @WithMockUser
        void returns400WhenNameMissing() throws Exception {
            ProductRequest request = new ProductRequest(
                    "", // empty name
                    "Description",
                    new BigDecimal("2.99"),
                    null, null, null, null, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 400 when price is missing")
        @WithMockUser
        void returns400WhenPriceMissing() throws Exception {
            ProductRequest request = new ProductRequest(
                    "Apple",
                    "Description",
                    null, // no price
                    null, null, null, null, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 400 when price is negative")
        @WithMockUser
        void returns400WhenPriceNegative() throws Exception {
            ProductRequest request = new ProductRequest(
                    "Apple",
                    "Description",
                    new BigDecimal("-1.00"), // negative price
                    null, null, null, null, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ========== PUT /api/products/{id} ==========

    @Nested
    @DisplayName("PUT /api/products/{id} - Update Product")
    class UpdateProduct {

        @Test
        @DisplayName("Updates product successfully")
        @WithMockUser
        void updatesProductSuccessfully() throws Exception {
            Product product = createAndSaveProduct("Old Name", new BigDecimal("1.00"));
            ProductRequest updateRequest = new ProductRequest(
                    "New Name",
                    "Updated description",
                    new BigDecimal("5.99"),
                    null, "kg", 200, "Canada", true);

            mockMvc.perform(put("/api/products/{id}", product.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("New Name")))
                    .andExpect(jsonPath("$.price", is(5.99)))
                    .andExpect(jsonPath("$.unit", is("kg")));
        }

        @Test
        @DisplayName("Returns 404 when updating non-existent product")
        @WithMockUser
        void returns404WhenUpdatingNonExistent() throws Exception {
            ProductRequest request = createValidProductRequest();

            mockMvc.perform(put("/api/products/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    // ========== DELETE /api/products/{id} ==========

    @Nested
    @DisplayName("DELETE /api/products/{id} - Delete Product")
    class DeleteProduct {

        @Test
        @DisplayName("Deletes product successfully (soft delete)")
        @WithMockUser
        void deletesProductSuccessfully() throws Exception {
            Product product = createAndSaveProduct("To Delete", new BigDecimal("1.00"));

            mockMvc.perform(delete("/api/products/{id}", product.getId()))
                    .andExpect(status().isNoContent());

            // Soft delete, so product is still retrievable but isActive is false
            mockMvc.perform(get("/api/products/{id}", product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isActive", is(false)));
        }

        @Test
        @DisplayName("Returns 404 when deleting non-existent product")
        @WithMockUser
        void returns404WhenDeletingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    // ========== GET /api/products/search ==========

    @Nested
    @DisplayName("GET /api/products/search - Search Products")
    class SearchProducts {

        @Test
        @DisplayName("Searches products by name successfully")
        void searchesByName() throws Exception {
            createAndSaveProduct("Red Apple", new BigDecimal("2.99"));
            createAndSaveProduct("Green Apple", new BigDecimal("2.49"));
            createAndSaveProduct("Banana", new BigDecimal("1.49"));

            mockMvc.perform(get("/api/products/search").param("name", "Apple"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Returns empty list when no match found")
        void returnsEmptyWhenNoMatch() throws Exception {
            createAndSaveProduct("Apple", new BigDecimal("2.99"));

            mockMvc.perform(get("/api/products/search").param("name", "Orange"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}
