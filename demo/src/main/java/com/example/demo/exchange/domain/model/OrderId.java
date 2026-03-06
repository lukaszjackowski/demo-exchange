package com.example.demo.exchange.domain.model;

import java.util.UUID;

public record OrderId(String value) {
    public static OrderId create() {
        return new OrderId(UUID.randomUUID().toString());
    }
}
