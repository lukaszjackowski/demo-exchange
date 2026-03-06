package com.example.demo.exchange.domain.handlers;


import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.example.demo.exchange.domain.model.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.example.demo.exchange.domain.eventprocessing.EventType.ORDER_NEW;

public class OrderPersistingHandler extends BaseEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OrderPersistingHandler.class);
    private final OrderRepository orderRepository;

    public OrderPersistingHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    protected Set<EventType> matchEvents() {
        return Set.of(ORDER_NEW);
    }

    @Override
    protected void onEvent(EngineEvent event, boolean endOfBatch) {
        try {
            LOG.info("Persisting Order with IdempotencyKey: {}", event.getClientOrderId());
            orderRepository.updateAll(event.getMatchingResult().getOrders());
        } catch (Throwable t) {
            LOG.error("Failed persisting orders", t);
        }
    }


}
