package com.example.demo.exchange.domain.model;

public record CreateOrderCommand(
        UserId userId,
        Side side,
        Asset asset,
        Price price,
        Quantity quantity,
        ClientOrderId clientOrderId
) {
}
