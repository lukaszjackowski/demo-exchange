package com.example.demo.exchange.adapters.postgres;

import com.example.demo.exchange.domain.eventprocessing.EventType;
import com.example.demo.exchange.domain.journal.Journal;
import com.example.demo.exchange.domain.journal.JournalEntry;
import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.ClientOrderId;
import com.example.demo.exchange.domain.model.Price;
import com.example.demo.exchange.domain.model.Quantity;
import com.example.demo.exchange.domain.model.Sequence;
import com.example.demo.exchange.domain.model.Side;
import com.example.demo.exchange.domain.model.UserId;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class PostgresJournal implements Journal {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PostgresJournal(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void store(JournalEntry event) {
        var params = new MapSqlParameterSource()
                .addValue("user_id", event.userId().value())
                .addValue("event_type", event.type().name())
                .addValue("price", event.price().amount())
                .addValue("quantity", event.quantity().value())
                .addValue("side", event.side().name())
                .addValue("asset", event.asset().name())
                .addValue("client_order_id", event.clientOrderId().value());

        namedParameterJdbcTemplate.update("""
                INSERT INTO events_journal (user_id, event_type, price, quantity, side, asset, client_order_id)
                VALUES (:user_id, :event_type, :price, :quantity, :side, :asset, :client_order_id)
                ON CONFLICT(id)
                DO NOTHING
                """, params);
    }

    @Override
    public List<JournalEntry> getAll() {
        return namedParameterJdbcTemplate.query(
                """
                    SELECT id, user_id, event_type, price, quantity, side, asset, client_order_id from events_journal
                    ORDER by id
                """,
                (rs, rowNum) -> new JournalEntry(
                        new Sequence(rs.getLong("id")),
                        new UserId(rs.getString("user_id")),
                        EventType.valueOf(rs.getString("event_type")),
                        new Price(rs.getBigDecimal("price")),
                        new Quantity(rs.getLong("quantity")),
                        Side.valueOf(rs.getString("side")),
                        Asset.valueOf(rs.getString("asset")),
                        new ClientOrderId(rs.getString("client_order_id"))
                )
        );
    }

}
