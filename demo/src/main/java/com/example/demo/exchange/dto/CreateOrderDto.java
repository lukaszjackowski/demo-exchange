package com.example.demo.exchange.dto;

import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.Side;

import java.math.BigDecimal;

public record CreateOrderDto(
        String userId,
        Side side,
        Asset asset,
        BigDecimal price,
        long quantity,
        String clientOrderId
) {
}
