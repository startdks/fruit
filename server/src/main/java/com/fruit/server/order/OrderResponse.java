package com.fruit.server.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        String shippingCity,
        String shippingState,
        String shippingZip,
        String shippingPhone,
        List<OrderItemResponse> items,
        LocalDateTime createdAt) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingZip(),
                order.getShippingPhone(),
                itemResponses,
                order.getCreatedAt());
    }
}
