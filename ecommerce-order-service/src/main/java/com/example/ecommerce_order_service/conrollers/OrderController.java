package com.example.ecommerce_order_service.conrollers;

import com.example.ecommerce_order_service.DTO.*;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OrderStatus;
import com.example.ecommerce_order_service.exceptions.OrderValidationException;
import com.example.ecommerce_order_service.exceptions.PaymentProcessingException;
import com.example.ecommerce_order_service.exceptions.RefundProcessingException;
import com.example.ecommerce_order_service.exceptions.ResourceNotFoundException;
import com.example.ecommerce_order_service.services.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for Managing Orders")
@SecurityRequirement(name = "bearerAuth") // <==== All endpoints require JWT **except where overridden**
public class OrderController {
    private final IOrderService orderService;

    public OrderController(@Qualifier("OrderServiceImpl") IOrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create a new order", description = "Creates an order for a user with a list of items.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<Order> createOrder(@RequestParam Long userId, @RequestBody List<OrderItemRequest> orderItems) {
        if (orderItems.isEmpty()) {
            throw new OrderValidationException("Order must contain at least one item.");
        }

        Order order = orderService.createOrder(userId, orderItems);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Retrieve a specific order by ID", description = "Returns details of an order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Get all orders for a user", description = "Retrieves all orders for a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get orders by status", description = "Retrieves all orders by their status (CREATED, PAID, SHIPPED, DELIVERED).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order status"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<OrderResponse> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            throw new OrderValidationException("Invalid order status: " + status);
        }
    }

    @Operation(summary = "Cancel an order", description = "Cancels an order if it has not been shipped or delivered.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN')") // Ensures only ADMIN can access this endpoint
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update order status", description = "Updates the status of an existing order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order status"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Process payment for an order", description = "Processes payment for an existing order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> processPayment(@PathVariable Long orderId, @RequestBody PaymentRequest paymentRequest) {
        try {
            orderService.processPayment(orderId, paymentRequest);
            return ResponseEntity.ok().build();
        } catch (PaymentProcessingException e) {
            throw new PaymentProcessingException("Payment failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Process refund for an order", description = "Processes a refund for an order that has been paid.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refund request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{orderId}/refund")
    @PreAuthorize("hasRole('ADMIN')") // Ensures only ADMIN can access this endpoint
    public ResponseEntity<Void> processRefund(@PathVariable Long orderId, @RequestBody RefundRequest refundRequest) {
        try {
            orderService.processRefund(orderId, refundRequest);
            return ResponseEntity.ok().build();
        } catch (RefundProcessingException e) {
            throw new RefundProcessingException("Refund failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Retrieve all order items by order ID", description = "Gets all items for a given order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order items retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemResponse> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @Operation(summary = "Remove an order item", description = "Removes an item from an order.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Order item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<Void> removeOrderItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {
        orderService.removeOrderItem(orderId, orderItemId);
        return ResponseEntity.noContent().build();
    }
}
