package com.example.demo.exchange.domain.journal;

import java.util.List;


public interface Journal {
    void store(JournalEntry journalEntry);
    List<JournalEntry> getAll();
}
