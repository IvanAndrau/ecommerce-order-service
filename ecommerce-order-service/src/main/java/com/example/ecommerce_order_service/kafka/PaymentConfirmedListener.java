package com.example.ecommerce_order_service.kafka;

import com.example.ecommerce_order_service.DTO.PaymentConfirmedEvent;
import com.example.ecommerce_order_service.entities.OrderStatus;
import com.example.ecommerce_order_service.services.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentConfirmedListener {
    private final IOrderService orderService;

    public PaymentConfirmedListener(@Qualifier("OrderServiceImpl") IOrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payment-confirmed", groupId = "order-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handlePaymentConfirmedEvent(PaymentConfirmedEvent event) {
        log.info("✅ Received payment-confirmed event: {}", event);

        if("PAID".equalsIgnoreCase(event.getStatus())) {
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.PAID);
            log.info("📦 Order #{} marked as PAID", event.getOrderId());
        } else {
            log.warn("⚠️ Unexpected payment status: {}", event.getStatus());
        }
    }
}
