package com.example.ecommerce_order_service.entities;

public enum OrderStatus {
    CREATED("CREATED"),
    PAID("PAID"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
