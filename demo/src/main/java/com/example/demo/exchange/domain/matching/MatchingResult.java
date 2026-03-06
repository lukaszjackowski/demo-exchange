package com.example.demo.exchange.domain.matching;

import com.example.demo.exchange.domain.model.Order;

import java.util.List;
import java.util.stream.Stream;

public record MatchingResult(Order order, List<Order> finalizedOrders) {

    public List<Order> getOrders() {
        return Stream.concat(Stream.of(order), finalizedOrders.stream()).toList();
    }
}
