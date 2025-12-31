package com.fruit.server.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
                Long id,
                String name,
                String description,
                BigDecimal price,
                String imageUrl,
                String unit,
                Integer stockQuantity,
                String origin,
                Boolean isActive,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

        public static ProductResponse from(Product product) {
                return new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice(),
                                product.getImageUrl(),
                                product.getUnit(),
                                product.getStockQuantity(),
                                product.getOrigin(),
                                product.getIsActive(),
                                product.getCreatedAt(),
                                product.getUpdatedAt());
        }
}
