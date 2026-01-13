package com.fruit.server.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrderAndGetResponse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam(required = false) Long userId) {
        List<OrderResponse> orders = (userId != null)
                ? orderService.getUserOrdersResponse(userId)
                : orderService.getAllOrdersResponse();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderByIdResponse(orderId);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatusResponse(orderId, status);
        return ResponseEntity.ok(order);
    }
}
