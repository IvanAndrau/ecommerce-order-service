package com.example.ecommerce_order_service.conrollers;

import com.example.ecommerce_order_service.DTO.*;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OrderStatus;
import com.example.ecommerce_order_service.exceptions.OrderValidationException;
import com.example.ecommerce_order_service.exceptions.PaymentProcessingException;
import com.example.ecommerce_order_service.exceptions.RefundProcessingException;
import com.example.ecommerce_order_service.exceptions.ResourceNotFoundException;
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
    public ResponseEntity<Order> createOrder(
            @RequestParam Long userId,
            @RequestBody List<OrderItemRequest> orderItems) {
        if (orderItems.isEmpty()) {
            throw new OrderValidationException("Order must contain at least one item.");
        }

        Order order = orderService.createOrder(userId, orderItems);
        return ResponseEntity.ok(order);
    }

    // Retrieve a specific order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);

        if (order == null) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        return ResponseEntity.ok(order);
    }

    // Get all orders for a user
    @GetMapping("user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // Get orders by status (CREATED, PAID, SHIPPED, DELIVERED)
    @GetMapping("status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<OrderResponse> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            throw new OrderValidationException("Invalid order status: " + status);
        }
    }

    // Cancel an order (if not shipped/delivered)
    @PatchMapping("{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // Update order status
    @PatchMapping("{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatus status
    ) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    // Process payment for an order
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest paymentRequest
    ) {
        try {
            orderService.processPayment(orderId, paymentRequest);
            return ResponseEntity.ok().build();
        } catch (PaymentProcessingException e) {
            throw new PaymentProcessingException("Payment failed: " + e.getMessage());
        }
    }

    // Process refund for an order
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Void> processRefund(
            @PathVariable Long orderId,
            @RequestBody RefundRequest refundRequest
    ) {
        try {
            orderService.processRefund(orderId, refundRequest);
            return ResponseEntity.ok().build();
        } catch (RefundProcessingException e) {
            throw new RefundProcessingException("Refund failed: " + e.getMessage());
        }
    }

    // Retrieve all order items by order ID
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItemsByOrderId(
            @PathVariable Long orderId
    ) {
        List<OrderItemResponse> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    // Remove an order item from an order
    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<Void> removeOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {
        orderService.removeOrderItem(orderId, orderItemId);
        return ResponseEntity.noContent().build();
    }





}
