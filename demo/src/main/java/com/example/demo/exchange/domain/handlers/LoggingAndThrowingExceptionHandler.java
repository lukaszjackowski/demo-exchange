package com.example.demo.exchange.domain.handlers;

import com.example.demo.exchange.domain.eventprocessing.EngineEvent;
import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAndThrowingExceptionHandler implements ExceptionHandler<EngineEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAndThrowingExceptionHandler.class);

    @Override
    public void handleEventException(Throwable ex, long sequence, EngineEvent event) {
        LOG.error("EventException: event {}", event);
        throw new RuntimeException(ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        throw new RuntimeException(ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        throw new RuntimeException(ex);
    }
}
