package com.example.demo.exchange.domain.model;

import java.math.BigDecimal;

public record Price(BigDecimal amount) implements Comparable<Price> {

    public Price {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + amount);
        }
    }

    @Override
    public int compareTo(Price o) {
        return this.amount.compareTo(o.amount);
    }
}
