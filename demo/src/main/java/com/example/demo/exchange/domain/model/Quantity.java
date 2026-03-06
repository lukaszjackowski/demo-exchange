package com.example.demo.exchange.domain.model;


public record Quantity(long value) implements Comparable<Quantity>  {

    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + value);
        }
    }

    @Override
    public int compareTo(Quantity o) {
        return Long.compare(value,o.value);
    }

    public static Quantity ZERO = new Quantity(0);
}
