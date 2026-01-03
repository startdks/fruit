package com.fruit.server.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        int itemCount) {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13");

    public static CartResponse from(List<CartItem> items) {
        List<CartItemResponse> itemResponses = items.stream()
                .map(CartItemResponse::from)
                .toList();

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        int count = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartResponse(itemResponses, subtotal, tax, total, count);
    }

}
