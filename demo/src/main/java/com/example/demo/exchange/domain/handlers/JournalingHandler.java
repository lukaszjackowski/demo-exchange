package com.example.demo.exchange.domain.handlers;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.example.demo.exchange.domain.journal.Journal;
import com.example.demo.exchange.domain.journal.JournalEntry;

import java.util.Set;

import static com.example.demo.exchange.domain.eventprocessing.EventType.ORDER_NEW;


public class JournalingHandler extends BaseEventHandler {

    private final Journal journal;

    public JournalingHandler(Journal journal) {
        this.journal = journal;
    }

    @Override
    protected Set<EventType> matchEvents() {
        return Set.of(ORDER_NEW);
    }

    @Override
    protected void onEvent(EngineEvent event, boolean endOfBatch) {
        if (!event.hasSequence()) {
            journal.store(toJournalEntry(event));
        }
    }

    private JournalEntry toJournalEntry(EngineEvent event) {
        return new JournalEntry(
                null,
                event.getUserId(),
                event.getType(),
                event.getPrice(),
                event.getQuantity(),
                event.getSide(),
                event.getAsset(),
                event.getClientOrderId());
    }
}
