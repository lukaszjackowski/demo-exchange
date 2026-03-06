package com.example.demo.exchange.domain.eventprocessing;

import com.example.demo.exchange.domain.journal.Journal;
import com.example.demo.exchange.domain.journal.JournalEntry;
import com.example.demo.exchange.domain.model.CreateOrderCommand;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EngineEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EngineEventProcessor.class);
    private final Disruptor<EngineEvent> disruptor;
    private final Journal journal;
    private final CountDownLatch replayingFinishedLatch;

    public EngineEventProcessor(Disruptor<EngineEvent> disruptor, Journal journal, CountDownLatch replayingFinishedLatch) {
        this.disruptor = disruptor;
        this.journal = journal;
        this.replayingFinishedLatch = replayingFinishedLatch;
    }

    public void process(CreateOrderCommand command) {
        disruptor.getRingBuffer().publishEvent((event, _) -> {
            event.create(command.userId(), command.price(), command.quantity(), command.side(), command.asset(), command.clientOrderId());
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void replayFromJournal() throws InterruptedException {
        LOG.info("Replaying started");

        var journalEntries = journal.getAll();
        journalEntries.forEach(this::replay);

        disruptor.getRingBuffer().publishEvent((event, _) -> {
            event.replay();
        });

        var replayingFinished = replayingFinishedLatch.await(5, TimeUnit.MINUTES);
        if (!replayingFinished) {
            throw new IllegalStateException("Replay wasn't finished within expected time");
        }

        LOG.info("Replaying finished. Replayed {} events", journalEntries.size());
    }

    private void replay(JournalEntry journalEntry) {
        disruptor.getRingBuffer().publishEvent((event, _) -> {
            event.create(
                    journalEntry.type(),
                    journalEntry.userId(),
                    journalEntry.price(),
                    journalEntry.quantity(),
                    journalEntry.side(),
                    journalEntry.asset(),
                    journalEntry.clientOrderId(),
                    journalEntry.sequence()
            );
        });
    }

}
