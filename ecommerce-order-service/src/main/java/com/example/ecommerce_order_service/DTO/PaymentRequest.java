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
public class PaymentRequest {
    private Long orderId;   // ID of the order being paid for
    private BigDecimal amount;  // Amount being paid
    private String paymentMethod; // e.g., "Credit Card", "PayPal"
}
