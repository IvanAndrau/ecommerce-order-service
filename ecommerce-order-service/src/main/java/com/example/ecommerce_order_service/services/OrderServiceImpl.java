package com.example.ecommerce_order_service.services;

import com.example.ecommerce_order_service.DTO.*;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OrderItem;
import com.example.ecommerce_order_service.entities.OrderStatus;
import com.example.ecommerce_order_service.repositories.OrderItemRepository;
import com.example.ecommerce_order_service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Order createOrder(long userId, List<OrderItemRequest> orderItems) {
        if(orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        // create a new order instance
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);

        // calculate total price
        double  totalAmount = 0;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderItems) {
            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            items.add(item);
            totalAmount += item.getPrice() * itemRequest.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(items);

        // save order to the database
        return orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus orderStatus) {
        return orderRepository.findByOrderStatus(orderStatus)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED){
            throw new RuntimeException("Cannot cancel an order that has already been shipped or delivered.");
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public List<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToOrderItemResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeOrderItem(Long orderId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        if(orderItem.getOrder().getId() != orderId){
            throw new IllegalArgumentException("Order item does not belong to the specified order.");
        }

        orderItemRepository.delete(orderItem);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void processPayment(Long orderId, PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(order.getOrderStatus() != OrderStatus.CREATED){
            throw new IllegalArgumentException("Order is not in a valid state for payment.");
        }

        // Simulate calling the payment service
        boolean paymentSuccessful = paymentRequest.getAmount()
                .compareTo(BigDecimal.valueOf(order.getTotalAmount())) >= 0;

        if(!paymentSuccessful){
            throw new IllegalArgumentException("Insufficient payment amount.");
        }

        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void processRefund(Long orderId, RefundRequest refundRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(order.getOrderStatus() != OrderStatus.PAID){
            throw new IllegalArgumentException("Cannot process refund for an unpaid order.");
        }
    }

    @Override
    public boolean orderExists(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    @Override
    public boolean isOrderOwnedByUser(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUserId().equals(userId))
                .orElse(false);
    }


    // Helper Methods to Convert Entities to DTOs
    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getUserId(), order.getOrderDate(),
                order.getTotalAmount(), order.getOrderStatus(),
                order.getOrderItems().stream().map(this::mapToOrderItemResponse).toList());
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(orderItem.getId(), orderItem.getProductId(),
                orderItem.getQuantity(), orderItem.getPrice());
    }
}
