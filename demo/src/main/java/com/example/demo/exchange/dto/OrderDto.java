package com.example.demo.exchange.dto;

import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.OrderStatus;
import com.example.demo.exchange.domain.model.Side;

import java.math.BigDecimal;

public record OrderDto(
        String id,
        String clientOrderId,
        String userId,
        Side side,
        BigDecimal price,
        long quantity,
        Asset asset,
        long remainingQuantity,
        OrderStatus status
) {
}
