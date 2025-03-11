package com.example.ecommerce_order_service.DTO;

import com.example.ecommerce_order_service.entities.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItemResponse> orderItems;
}
