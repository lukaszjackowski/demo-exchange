package com.example.demo.exchange.domain.model;

import java.math.BigDecimal;

public record Price(BigDecimal amount) implements Comparable<Price> {



    @Override
    public int compareTo(Price o) {
        return this.amount.compareTo(o.amount);
    }
}
