package com.example.demo.exchange.domain.handlers;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.example.demo.exchange.domain.model.ClientOrderId;

import java.util.HashSet;
import java.util.Set;

import static com.example.demo.exchange.domain.eventprocessing.EventType.ORDER_NEW;

public class OrderIdempotencyHandler extends BaseEventHandler {

    private final Set<ClientOrderId> processed = new HashSet<>();

    @Override
    protected Set<EventType> matchEvents() {
        return Set.of(ORDER_NEW);
    }

    @Override
    protected void onEvent(EngineEvent event, boolean endOfBatch) {
        if (processed.contains(event.getClientOrderId())) {
            event.ignore();
        } else {
            processed.add(event.getClientOrderId());
        }
    }
}
