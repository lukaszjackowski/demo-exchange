package com.example.demo.exchange.domain.matching;

import com.example.demo.exchange.domain.model.Order;
import com.example.demo.exchange.domain.model.OrderId;
import com.example.demo.exchange.domain.model.OrderStatus;
import com.example.demo.exchange.domain.model.Price;
import com.example.demo.exchange.domain.model.Quantity;
import com.example.demo.exchange.domain.model.Side;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class MatchingEngine {

    private final TreeMap<Price, LinkedHashMap<OrderId, Order>> buyOrders;
    private final TreeMap<Price, LinkedHashMap<OrderId, Order>> sellOrders;

    public MatchingEngine() {
        this.buyOrders = new TreeMap<>(Collections.reverseOrder());
        this.sellOrders = new TreeMap<>();
    }

    public MatchingResult match(Order order) {

        var oppositeOrders = order.getSide() == Side.BUY ? sellOrders : buyOrders;
        var updatedOrders = new LinkedList<Order>();
        var remainingQuantity = order.getRemainingQuantity().value();

        Price lastPrice = null;
        while (remainingQuantity > 0 && !oppositeOrders.isEmpty()) {
            var bestPrice = lastPrice == null ? oppositeOrders.firstKey() : oppositeOrders.higherKey(lastPrice);

            if (bestPrice == null) break;
            if (!isPriceMatching(order.getPrice(), bestPrice, order.getSide())) break;

            var ordersAtBestPrice = oppositeOrders.get(bestPrice);

            var iterator = ordersAtBestPrice.values().iterator();
            while (iterator.hasNext() && remainingQuantity > 0) {
                var oppositeOrder = iterator.next();
                if (oppositeOrder.getUserId().equals(order.getUserId())) continue;
                var tradeQuantity = Math.min(remainingQuantity, oppositeOrder.getRemainingQuantity().value());
                remainingQuantity -= tradeQuantity;
                oppositeOrder.reduceRemainingQuantity(new Quantity(tradeQuantity));
                updatedOrders.add(oppositeOrder);
                if (oppositeOrder.getStatus() == OrderStatus.FILLED) {
                    iterator.remove();
                }
            }

            if (ordersAtBestPrice.isEmpty()) {
                oppositeOrders.remove(bestPrice);
            }
            lastPrice = bestPrice;

        }

        order.updateRemainingQuantity(new Quantity(remainingQuantity));
        if (order.getStatus() != OrderStatus.FILLED) {
            var orders = order.getSide() == Side.BUY ? buyOrders : sellOrders;
            orders.computeIfAbsent(order.getPrice(), _ -> new LinkedHashMap<>())
                    .put(order.getId(), order);
        }

        return new MatchingResult(order, updatedOrders);
    }

    private boolean isPriceMatching(Price orderPrice, Price bestPrice, Side side) {
        return side == Side.BUY ? orderPrice.compareTo(bestPrice) >=0 : orderPrice.compareTo(bestPrice) <= 0;
    }
}
