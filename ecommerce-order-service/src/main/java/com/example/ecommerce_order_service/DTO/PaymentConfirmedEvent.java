package com.example.ecommerce_order_service.DTO;

import lombok.Data;

@Data
public class PaymentConfirmedEvent {
    private Long orderId;
    private String status;
}
