package com.example.demo.exchange.domain;

import com.example.demo.exchange.domain.journal.Journal;
import com.example.demo.exchange.domain.journal.JournalEntry;
import com.example.demo.exchange.domain.model.Sequence;

import java.util.ArrayList;
import java.util.List;

public class InMemoryJournal implements Journal {

    long sequence = 0;
    List<JournalEntry> journalEntries = new ArrayList<>();


    @Override
    public void store(JournalEntry journalEntry) {
        journalEntries.add(withSequence(journalEntry, new Sequence(sequence++)));
    }

    @Override
    public List<JournalEntry> getAll() {
        return journalEntries;
    }

    public JournalEntry withSequence(JournalEntry journalEntry, Sequence generatedSequence) {
        return new JournalEntry(
                generatedSequence,
                journalEntry.userId(),
                journalEntry.type(),
                journalEntry.price(),
                journalEntry.quantity(),
                journalEntry.side(),
                journalEntry.asset(),
                journalEntry.clientOrderId());
    }
}
