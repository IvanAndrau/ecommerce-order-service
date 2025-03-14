package com.example.ecommerce_order_service.conrollers;

import com.example.ecommerce_order_service.DTO.*;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OrderStatus;
import com.example.ecommerce_order_service.services.IOrderService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final IOrderService orderService;

    public OrderController(@Qualifier("OrderServiceImpl") IOrderService orderService) {
        this.orderService = orderService;
    }

    // Create a new order
    @PostMapping()
    public ResponseEntity<Order> createOrder(@RequestParam Long userId, @RequestBody List<OrderItemRequest> orderItems) {
        Order order = orderService.createOrder(userId, orderItems);
        return ResponseEntity.ok(order);
    }

    // Retrieve a specific order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // Get all orders for a user
    @GetMapping("user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // Get orders by status (CREATED, PAID, SHIPPED, DELIVERED)
    @GetMapping("status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> order = orderService.getOrdersByStatus(OrderStatus.valueOf(status));
        return ResponseEntity.ok(order);
    }

    // Cancel an order (if not shipped/delivered)
    @PatchMapping("{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // Update order status
    @PatchMapping("{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    // Process payment for an order
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> processPayment(@PathVariable Long orderId, @RequestBody PaymentRequest paymentRequest) {
        orderService.processPayment(orderId, paymentRequest);
        return ResponseEntity.ok().build();
    }

    // Process refund for an order
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Void> processRefund(@PathVariable Long orderId, @RequestBody RefundRequest refundRequest) {
        orderService.processRefund(orderId, refundRequest);
        return ResponseEntity.ok().build();
    }

    // Retrieve all order items by order ID
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemResponse> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    // Remove an order item from an order
    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<Void> removeOrderItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {
        orderService.removeOrderItem(orderId, orderItemId);
        return ResponseEntity.noContent().build();
    }





}
