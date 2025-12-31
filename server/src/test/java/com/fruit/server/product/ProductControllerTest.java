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
    @DisplayName("GET /api/products - 전체 상품 조회")
    class GetAllProducts {

        @Test
        @DisplayName("상품이 없을 때 빈 배열 반환")
        void returnsEmptyListWhenNoProducts() throws Exception {
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("상품 목록 조회 성공")
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
        @DisplayName("활성 상품만 조회")
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
    @DisplayName("GET /api/products/{id} - 상품 상세 조회")
    class GetProductById {

        @Test
        @DisplayName("존재하는 상품 조회 성공")
        void returnsProductWhenExists() throws Exception {
            Product product = createAndSaveProduct("Apple", new BigDecimal("2.99"));

            mockMvc.perform(get("/api/products/{id}", product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                    .andExpect(jsonPath("$.name", is("Apple")))
                    .andExpect(jsonPath("$.price", is(2.99)));
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회시 404 반환")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/products/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    // ========== POST /api/products ==========

    @Nested
    @DisplayName("POST /api/products - 상품 생성")
    class CreateProduct {

        @Test
        @DisplayName("유효한 요청으로 상품 생성 성공")
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
        @DisplayName("이름 없이 요청시 400 반환")
        void returns400WhenNameMissing() throws Exception {
            ProductRequest request = new ProductRequest(
                    "", // 빈 이름
                    "Description",
                    new BigDecimal("2.99"),
                    null, null, null, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("가격 없이 요청시 400 반환")
        void returns400WhenPriceMissing() throws Exception {
            ProductRequest request = new ProductRequest(
                    "Apple",
                    "Description",
                    null, // 가격 없음
                    null, null, null, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("음수 가격으로 요청시 400 반환")
        void returns400WhenPriceNegative() throws Exception {
            ProductRequest request = new ProductRequest(
                    "Apple",
                    "Description",
                    new BigDecimal("-1.00"), // 음수 가격
                    null, null, null, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ========== PUT /api/products/{id} ==========

    @Nested
    @DisplayName("PUT /api/products/{id} - 상품 수정")
    class UpdateProduct {

        @Test
        @DisplayName("상품 수정 성공")
        void updatesProductSuccessfully() throws Exception {
            Product product = createAndSaveProduct("Old Name", new BigDecimal("1.00"));
            ProductRequest updateRequest = new ProductRequest(
                    "New Name",
                    "Updated description",
                    new BigDecimal("5.99"),
                    null, "kg", 200, true);

            mockMvc.perform(put("/api/products/{id}", product.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("New Name")))
                    .andExpect(jsonPath("$.price", is(5.99)))
                    .andExpect(jsonPath("$.unit", is("kg")));
        }

        @Test
        @DisplayName("존재하지 않는 상품 수정시 404 반환")
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
    @DisplayName("DELETE /api/products/{id} - 상품 삭제")
    class DeleteProduct {

        @Test
        @DisplayName("상품 삭제 성공 (soft delete)")
        void deletesProductSuccessfully() throws Exception {
            Product product = createAndSaveProduct("To Delete", new BigDecimal("1.00"));

            mockMvc.perform(delete("/api/products/{id}", product.getId()))
                    .andExpect(status().isNoContent());

            // Soft delete이므로 조회는 가능하지만 isActive가 false
            mockMvc.perform(get("/api/products/{id}", product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isActive", is(false)));
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제시 404 반환")
        void returns404WhenDeletingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    // ========== GET /api/products/search ==========

    @Nested
    @DisplayName("GET /api/products/search - 상품 검색")
    class SearchProducts {

        @Test
        @DisplayName("이름으로 검색 성공")
        void searchesByName() throws Exception {
            createAndSaveProduct("Red Apple", new BigDecimal("2.99"));
            createAndSaveProduct("Green Apple", new BigDecimal("2.49"));
            createAndSaveProduct("Banana", new BigDecimal("1.49"));

            mockMvc.perform(get("/api/products/search").param("name", "Apple"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("검색 결과 없을 때 빈 배열 반환")
        void returnsEmptyWhenNoMatch() throws Exception {
            createAndSaveProduct("Apple", new BigDecimal("2.99"));

            mockMvc.perform(get("/api/products/search").param("name", "Orange"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}
