package com.example.ecommerce_order_service.repositories;

import com.example.ecommerce_order_service.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Find all items for a given order
    List<OrderItem> findByOrderId(Long orderId);

    // Delete all items for a given order
    void deleteByOrderId(Long orderId);
}
