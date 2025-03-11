package com.example.ecommerce_order_service.services;

import com.example.ecommerce_order_service.DTO.*;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OrderItem;
import com.example.ecommerce_order_service.entities.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IOrderService {
    Order createOrder(long userId, List<OrderItemRequest> orderItems); // Create a New Order

    List<OrderResponse> getOrdersByUserId(Long userId); // Get Orders by User ID

    OrderResponse getOrderById(Long orderId); // Get Order by ID

    List<OrderResponse> getOrdersByStatus(OrderStatus orderStatus); // Retrieves all orders based on status (CREATED, PAID, SHIPPED, DELIVERED)

    void cancelOrder(Long orderId); // Allows users to cancel an order if it’s not shipped

    List<OrderItemResponse> getOrderItemsByOrderId(Long orderId); // Fetches all items belonging to an order

    void removeOrderItem(Long orderId, Long orderItemId); // Removes an item from an order

    Order updateOrderStatus(Long orderId, OrderStatus orderStatus); // Updates order status (PAID, SHIPPED, DELIVERED)

    void processPayment(Long orderId, PaymentRequest paymentRequest); // Calls ecommerce-payment-service to handle payment

    void processRefund(Long orderId, RefundRequest refundRequest); // Calls payment service for a refund

    boolean orderExists(Long orderId); // Ensures an order exists before performing actions

    boolean isOrderOwnedByUser(Long orderId, Long userId); // Prevents unauthorized order access
}
