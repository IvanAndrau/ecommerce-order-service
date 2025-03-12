package com.example.ecommerce_order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequest {
    private Long paymentId;   // ID of the payment being refunded
    private BigDecimal refundAmount;  // Amount to be refunded
    private String reason; // Optional reason for the refund
}
