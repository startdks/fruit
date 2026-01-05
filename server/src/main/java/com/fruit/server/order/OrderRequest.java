package com.fruit.server.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
                Long userId,

                @NotEmpty(message = "Order must contain at least one item") @Valid List<OrderItemRequest> items,

                @NotNull(message = "Shipping address is required") String shippingAddress,

                String shippingCity,
                String shippingState,
                String shippingZip,
                String shippingPhone) {
}
