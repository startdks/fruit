package com.fruit.server.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required") @Size(max = 100, message = "Product name cannot exceed 100 characters") String name,

        String description,

        @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") BigDecimal price,

        String imageUrl,

        @Size(max = 20, message = "Unit cannot exceed 20 characters") String unit,

        @PositiveOrZero(message = "Stock quantity must be 0 or greater") Integer stockQuantity,

        Boolean isActive) {

    public ProductRequest {
        if (unit == null) {
            unit = "lb";
        }
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}