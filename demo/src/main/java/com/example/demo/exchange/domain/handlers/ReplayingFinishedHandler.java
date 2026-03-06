package com.example.demo.exchange.domain.handlers;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EventType;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static com.example.demo.exchange.domain.eventprocessing.EventType.REPLAYING;

public class ReplayingFinishedHandler extends BaseEventHandler {

    private final CountDownLatch latch;

    public ReplayingFinishedHandler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    protected Set<EventType> matchEvents() {
        return Set.of(REPLAYING);
    }

    @Override
    protected void onEvent(EngineEvent event, boolean endOfBatch) {
        latch.countDown();
    }
}
