package com.example.demo.exchange.domain.model;

import java.util.List;

public interface OrderRepository {
    void updateAll( List<Order> orders);
    List<Order> getOrders(UserId userId);
}
