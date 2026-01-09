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
        // Check if products already exist
        if (productRepository.count() > 0) {
            return;
        }

        // Initialize sample products with real image URLs
        createProduct("Fresh Apples", "Crisp and juicy red apples", "4.99",
                "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400", 100);
        createProduct("Sweet Oranges", "Vitamin C rich oranges", "3.99",
                "https://images.unsplash.com/photo-1547514701-42782101795e?w=400", 150);
        createProduct("Ripe Bananas", "Perfectly ripened bananas", "2.99",
                "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400", 200);
        createProduct("Fresh Strawberries", "Sweet and succulent berries", "5.99",
                "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400", 80);
        createProduct("Juicy Grapes", "Seedless premium grapes", "4.49",
                "https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=400", 120);
        createProduct("Tropical Mangoes", "Sweet tropical mangoes", "6.99",
                "https://images.unsplash.com/photo-1553279768-865429fa0078?w=400", 60);
        createProduct("Fresh Pineapples", "Tropical sweet pineapples", "5.49",
                "https://images.unsplash.com/photo-1550258987-190a2d41a8ba?w=400", 70);
        createProduct("Sweet Watermelons", "Refreshing summer watermelons", "3.49",
                "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400", 90);
        System.out.println("Database initialized with sample products!");
    }

    private void createProduct(String name, String description, String price, String imageUrl, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setImageUrl(imageUrl);
        product.setUnit("lb");
        product.setStockQuantity(stock);
        product.setIsActive(true);
        productRepository.save(product);
    }
}
