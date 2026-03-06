package com.example.demo.exchange.domain.eventprocessing;

import com.example.demo.exchange.domain.matching.MatchingResult;
import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.ClientOrderId;
import com.example.demo.exchange.domain.model.Price;
import com.example.demo.exchange.domain.model.Quantity;
import com.example.demo.exchange.domain.model.Sequence;
import com.example.demo.exchange.domain.model.Side;
import com.example.demo.exchange.domain.model.UserId;

import static com.example.demo.exchange.domain.eventprocessing.EventType.IGNORING;
import static com.example.demo.exchange.domain.eventprocessing.EventType.ORDER_NEW;
import static com.example.demo.exchange.domain.eventprocessing.EventType.REPLAYING;

public class EngineEvent {

    private UserId userId;
    private EventType type;
    private Price price;
    private Quantity quantity;
    private Side side;
    private Asset asset;
    private ClientOrderId clientOrderId;
    private MatchingResult matchingResult;
    private Sequence sequence;

    public UserId getUserId() {
        return userId;
    }

    public EventType getType() {
        return type;
    }

    public Price getPrice() {
        return price;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Side getSide() {
        return side;
    }

    public Asset getAsset() {
        return asset;
    }

    public ClientOrderId getClientOrderId() {
        return clientOrderId;
    }

    public boolean hasSequence() {
        return this.sequence != null;
    }

    public MatchingResult getMatchingResult() {
        return matchingResult;
    }

    public void create(UserId userId, Price price, Quantity qty, Side side, Asset asset, ClientOrderId clientOrderId) {
        create(ORDER_NEW, userId, price, qty, side, asset, clientOrderId, null);
    }

    public void create(EventType eventType, UserId userId, Price price, Quantity qty, Side side, Asset asset, ClientOrderId clientOrderId, Sequence sequence) {
        this.type = eventType;
        this.userId = userId;
        this.price = price;
        this.quantity = qty;
        this.side = side;
        this.asset = asset;
        this.clientOrderId = clientOrderId;
        this.type = ORDER_NEW;
        this.sequence = sequence;
    }

    public void assignMatchingResult(MatchingResult matchingResult) {
        this.matchingResult = matchingResult;
    }

    public void ignore() {
        this.type = IGNORING;
    }

    public void replay() {
        this.type = REPLAYING;
    }

    public void clean() {
        this.userId = null;
        this.type = null;
        this.price = null;
        this.quantity = null;
        this.side = null;
        this.asset = null;
        this.clientOrderId = null;
        this.matchingResult = null;
        this.sequence = null;
    }

    @Override
    public String toString() {
        return "EngineEvent{" +
                "userId=" + userId +
                ", type=" + type +
                ", price=" + price +
                ", quantity=" + quantity +
                ", side=" + side +
                ", asset=" + asset +
                ", clientOrderId=" + clientOrderId +
                ", matchingResult=" + matchingResult +
                '}';
    }
}
