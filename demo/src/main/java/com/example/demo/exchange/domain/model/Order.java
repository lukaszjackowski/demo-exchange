package com.example.demo.exchange.domain.model;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;

import java.util.Objects;

import static com.example.demo.exchange.domain.model.OrderStatus.NEW;

public class Order {

    private final OrderId id;
    private final ClientOrderId clientOrderId;
    private final UserId userId;
    private final Side side;
    private final Price price;
    private final Quantity quantity;
    private final Asset asset;
    private Quantity remainingQuantity;
    private OrderStatus status;

    public Order(OrderId id, ClientOrderId clientOrderId, UserId userId, Side side, Price price, Quantity quantity, Asset asset, Quantity remainingQuantity, OrderStatus status) {
        this.id = id;
        this.clientOrderId = clientOrderId;
        this.userId = userId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.asset = asset;
        this.remainingQuantity = remainingQuantity;
        this.status = status;
    }

    public OrderId getId() {
        return id;
    }

    public ClientOrderId getClientOrderId() {
        return clientOrderId;
    }

    public UserId getUserId() {
        return userId;
    }

    public Side getSide() {
        return side;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Asset getAsset() {
        return asset;
    }

    public Quantity getRemainingQuantity() {
        return remainingQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Price getPrice() {
        return price;
    }

    public void updateRemainingQuantity(Quantity updatedRemainingQuantity) {
        this.remainingQuantity = updatedRemainingQuantity;
        updateOrderStatus();
    }

    public void reduceRemainingQuantity(Quantity quantityToReduce) {
        this.remainingQuantity = new Quantity(this.remainingQuantity.value() - quantityToReduce.value());
        updateOrderStatus();
    }

    private void updateOrderStatus() {
        if (this.remainingQuantity.compareTo(Quantity.ZERO) == 0) {
            this.status = OrderStatus.FILLED;
        } else if (this.remainingQuantity.compareTo(this.quantity) == 0) {
            this.status = OrderStatus.NEW;
        } else {
            this.status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(userId, order.userId) &&
                side == order.side &&
                Objects.equals(quantity, order.quantity) &&
                Objects.equals(asset, order.asset) &&
                Objects.equals(remainingQuantity, order.remainingQuantity) &&
                Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, side, quantity, asset, remainingQuantity, status);
    }

    public static Order createNewOrderFromEngineEvent(EngineEvent engineEvent) {
        return new Order(
                OrderId.create(),
                engineEvent.getClientOrderId(),
                engineEvent.getUserId(),
                engineEvent.getSide(),
                engineEvent.getPrice(),
                engineEvent.getQuantity(),
                engineEvent.getAsset(),
                engineEvent.getQuantity(),
                NEW);
    }
}
