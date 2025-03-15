package com.example.ecommerce_order_service.exceptions;

public class RefundProcessingException extends RuntimeException {
    public RefundProcessingException(String message) {
        super(message);
    }
}
