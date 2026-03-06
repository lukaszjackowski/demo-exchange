package com.example.demo.exchange.domain;

import com.example.demo.exchange.domain.model.Order;
import com.example.demo.exchange.domain.model.OrderId;
import com.example.demo.exchange.domain.model.OrderRepository;
import com.example.demo.exchange.domain.model.UserId;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new HashMap<>();
    private Optional<CountDownLatch> latch = Optional.empty();

    public void setLatch(CountDownLatch latch) {
        this.latch = Optional.of(latch);
    }

    @Override
    public void updateAll(List<Order> orders) {
        orders.forEach(it -> {
            this.orders.put(it.getId(), it);
        });
        latch.ifPresent(CountDownLatch::countDown);
    }

    @Override
    public List<Order> getOrders(UserId userId) {
        return this.orders.values().stream().filter(it -> it.getUserId().equals(userId)).toList();
    }
}
