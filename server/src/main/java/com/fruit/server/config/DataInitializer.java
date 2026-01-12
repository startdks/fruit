package com.fruit.server.config;

import com.fruit.server.product.Product;
import com.fruit.server.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // 이미 상품이 있으면 초기화 스킵
        if (productRepository.count() > 0) {
            System.out.println("✅ Products already exist. Skipping initialization.");
            return;
        }

        // Initialize sample products with single fruit on white background (local
        // images)
        createProduct("Fresh Apples", "Crisp and juicy red apples", "4.99",
                "/images/product-apple.png", 100, "South Korea");
        createProduct("Sweet Oranges", "Vitamin C rich oranges", "3.99",
                "/images/product-orange.png", 150, "USA");
        createProduct("Ripe Bananas", "Perfectly ripened bananas", "2.99",
                "/images/product-banana.png", 200, "Philippines");
        createProduct("Fresh Strawberries", "Sweet and succulent berries", "5.99",
                "/images/product-strawberry.png", 80, "Japan");
        createProduct("Juicy Grapes", "Seedless premium grapes", "4.49",
                "/images/product-grapes.png", 120, "Chile");
        createProduct("Tropical Mangoes", "Sweet tropical mangoes", "6.99",
                "/images/product-mango.png", 60, "Thailand");
        createProduct("Fresh Pineapples", "Tropical sweet pineapples", "5.49",
                "/images/product-pineapple.png", 70, "Philippines");
        createProduct("Sweet Watermelons", "Refreshing summer watermelons", "3.49",
                "/images/product-watermelon.png", 90,
                "Taiwan");

        System.out.println("✅ Database initialized with sample products!");
    }

    private void createProduct(String name, String description, String price, String imageUrl, int stock,
            String origin) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setImageUrl(imageUrl);
        product.setUnit("lb");
        product.setStockQuantity(stock);
        product.setOrigin(origin);
        product.setIsActive(true);
        productRepository.save(product);
    }
}
