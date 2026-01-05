package com.fruit.server.order;

import com.fruit.server.product.Product;
import com.fruit.server.product.ProductService;
import com.fruit.server.user.User;
import com.fruit.server.user.UserService;
import com.fruit.server.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    // Public methods (returns DTO)

    // Creates an order and returns response DTO
    @Transactional
    public OrderResponse createOrderAndGetResponse(OrderRequest request) {
        Order order = createOrder(request);
        return OrderResponse.from(order);
    }

    // Gets user's order list
    public List<OrderResponse> getUserOrdersResponse(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    // Gets order by ID
    public OrderResponse getOrderByIdResponse(Long orderId) {
        Order order = getOrderById(orderId);
        return OrderResponse.from(order);
    }

    // Gets all orders (admin)
    public List<OrderResponse> getAllOrdersResponse() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    // Updates order status
    @Transactional
    public OrderResponse updateOrderStatusResponse(Long orderId, OrderStatus status) {
        Order order = updateOrderStatus(orderId, status);
        return OrderResponse.from(order);
    }

    // Internal methods (returns Entity)

    // Creates an order
    @Transactional
    Order createOrder(OrderRequest request) {
        Order order = new Order();

        if (request.userId() != null) {
            User user = userService.findById(request.userId()).orElse(null);
            order.setUser(user);
        }

        order.setShippingAddress(request.shippingAddress());
        order.setShippingCity(request.shippingCity());
        order.setShippingState(request.shippingState());
        order.setShippingZip(request.shippingZip());
        order.setShippingPhone(request.shippingPhone());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productService.findProductById(itemRequest.productId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setUnitPrice(product.getPrice());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        if (request.userId() != null) {
            cartService.clearCart(request.userId());
        }

        return savedOrder;
    }

    // Gets order by ID
    Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    // Updates order status
    @Transactional
    Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
