package com.example.demo.exchange.domain.handlers;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.lmax.disruptor.EventHandler;


public class CleaningHandler implements EventHandler<EngineEvent> {

    @Override
    public void onEvent(EngineEvent event, long sequence, boolean endOfBatch) {
        event.clean();
    }
}
