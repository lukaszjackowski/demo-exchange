package com.example.demo.exchange.domain.handlers;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.lmax.disruptor.EventHandler;

import java.util.Set;

public abstract class BaseEventHandler implements EventHandler<EngineEvent> {

    protected Set<EventType> matchEvents() {
        return Set.of();
    };

    protected abstract void onEvent(EngineEvent event, boolean endOfBatch);

    @Override
    public void onEvent(EngineEvent event, long sequence, boolean endOfBatch) {
        if (matchEvents().contains(event.getType())) {
            onEvent(event, endOfBatch);
        }
    }
}
