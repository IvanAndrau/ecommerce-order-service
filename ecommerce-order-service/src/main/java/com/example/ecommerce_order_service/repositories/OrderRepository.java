package com.example.ecommerce_order_service.repositories;

import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by User ID
    List<Order> findByUserId(Long userId);

    // Find orders by Status
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Check if an order exists by ID
    boolean existsById(Long id);
}
