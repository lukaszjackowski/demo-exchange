package com.example.demo.exchange.domain.journal;

import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.ClientOrderId;
import com.example.demo.exchange.domain.model.Price;
import com.example.demo.exchange.domain.model.Quantity;
import com.example.demo.exchange.domain.model.Sequence;
import com.example.demo.exchange.domain.model.Side;
import com.example.demo.exchange.domain.model.UserId;

public record JournalEntry(
        Sequence sequence,
        UserId userId,
        EventType type,
        Price price,
        Quantity quantity,
        Side side,
        Asset asset,
        ClientOrderId clientOrderId)
{
}
