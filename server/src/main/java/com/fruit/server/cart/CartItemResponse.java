package com.fruit.server.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal productPrice,
        String productImageUrl,
        String productUnit,
        Integer quantity,
        BigDecimal priceAtAddition,
        BigDecimal subtotal) {
    public static CartItemResponse from(CartItem item) {
        BigDecimal price = item.getPriceAtAddition() != null
                ? item.getPriceAtAddition()
                : item.getProduct().getPrice();
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getProduct().getImageUrl(),
                item.getProduct().getUnit(),
                item.getQuantity(),
                item.getPriceAtAddition(),
                price.multiply(BigDecimal.valueOf(item.getQuantity())));
    }

}
