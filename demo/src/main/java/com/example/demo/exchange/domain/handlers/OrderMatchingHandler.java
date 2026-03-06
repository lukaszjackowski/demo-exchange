package com.example.demo.exchange.domain.handlers;


import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.example.demo.exchange.domain.matching.MatchingEngine;
import com.example.demo.exchange.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.example.demo.exchange.domain.eventprocessing.EventType.ORDER_NEW;

public class OrderMatchingHandler extends BaseEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OrderMatchingHandler.class);
    private final MatchingEngine matchingEngine;

    public OrderMatchingHandler(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    @Override
    protected Set<EventType> matchEvents() {
        return Set.of(ORDER_NEW);
    }

    @Override
    protected void onEvent(EngineEvent event, boolean endOfBatch) {
        LOG.info("Matching Order: {}", event.getClientOrderId());
        var matchingResult = matchingEngine.match(Order.createNewOrderFromEngineEvent(event));
        event.assignMatchingResult(matchingResult);
    }

}
