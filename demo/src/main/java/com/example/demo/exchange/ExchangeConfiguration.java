package com.example.demo.exchange;

import com.example.demo.exchange.adapters.postgres.PostgresJournal;
import com.example.demo.exchange.adapters.postgres.PostgresOrderRepository;
import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.example.demo.exchange.domain.eventprocessing.EngineEventProcessor;
import com.example.demo.exchange.domain.handlers.JournalingHandler;
import com.example.demo.exchange.domain.handlers.LoggingAndThrowingExceptionHandler;
import com.example.demo.exchange.domain.handlers.OrderIdempotencyHandler;
import com.example.demo.exchange.domain.handlers.OrderMatchingHandler;
import com.example.demo.exchange.domain.handlers.OrderPersistingHandler;
import com.example.demo.exchange.domain.handlers.ReplayingFinishedHandler;
import com.example.demo.exchange.domain.journal.Journal;
import com.example.demo.exchange.domain.matching.MatchingEngine;
import com.example.demo.exchange.domain.model.OrderRepository;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ExchangeConfiguration {

    private static final int DISRUPTOR_BUFFER_SIZE = 1024;

    @Bean
    public Journal journal(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new PostgresJournal(namedParameterJdbcTemplate);
    }

    @Bean
    public OrderRepository orderRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new PostgresOrderRepository(namedParameterJdbcTemplate);
    }

    @Bean
    public ExchangeFacade exchangeFacade(OrderRepository orderRepository, EngineEventProcessor engineEventProcessor) {
        return new ExchangeFacade(engineEventProcessor, orderRepository);
    }

    @Bean
    public EngineEventProcessor engineEventProcesesor(Journal journal, OrderRepository orderRepository) {

        ThreadFactory threadFactory = r -> new Thread(r, "MatchingEngineThread");

        Disruptor<EngineEvent> disruptor = new Disruptor<>(
                EngineEvent::new,
                DISRUPTOR_BUFFER_SIZE,
                threadFactory,
                ProducerType.MULTI,
                new BlockingWaitStrategy()
        );

        var replayingFinishedLatch = new CountDownLatch(1);

        disruptor.handleEventsWith(new ReplayingFinishedHandler(replayingFinishedLatch))
                .then(new OrderIdempotencyHandler())
                .then(new JournalingHandler(journal))
                .then(new OrderMatchingHandler(new MatchingEngine()))
                .then(new OrderPersistingHandler(orderRepository));

        disruptor.setDefaultExceptionHandler(new LoggingAndThrowingExceptionHandler());

        disruptor.start();

        return new EngineEventProcessor(disruptor, journal, replayingFinishedLatch);
    }

}
