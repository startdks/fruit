package com.fruit.server.order;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "Product ID is required") Long productId,
        @NotNull(message = "Quantity is required") Integer quantity) {
}
