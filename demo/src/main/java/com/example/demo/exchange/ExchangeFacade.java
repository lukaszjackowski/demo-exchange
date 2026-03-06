package com.example.demo.exchange;

import com.example.demo.exchange.domain.eventprocessing.EngineEventProcessor;
import com.example.demo.exchange.domain.model.ClientOrderId;
import com.example.demo.exchange.domain.model.CreateOrderCommand;
import com.example.demo.exchange.domain.model.OrderRepository;
import com.example.demo.exchange.domain.model.Price;
import com.example.demo.exchange.domain.model.Quantity;
import com.example.demo.exchange.domain.model.UserId;
import com.example.demo.exchange.dto.CreateOrderDto;
import com.example.demo.exchange.dto.OrderDto;
import com.example.demo.exchange.dto.OrdersDto;

public class ExchangeFacade {

    private final EngineEventProcessor engineEventProcessor;
    private final OrderRepository orderRepository;

    public ExchangeFacade(EngineEventProcessor engineEventProcessor, OrderRepository orderRepository) {
        this.engineEventProcessor = engineEventProcessor;
        this.orderRepository = orderRepository;
    }

    public void createOrder(CreateOrderDto createOrderDto) {
        engineEventProcessor.process(
                new CreateOrderCommand(
                        new UserId(createOrderDto.userId()),
                        createOrderDto.side(),
                        createOrderDto.asset(),
                        new Price(createOrderDto.price()),
                        new Quantity(createOrderDto.quantity()),
                        new ClientOrderId(createOrderDto.clientOrderId())
                )
        );
    }

    public OrdersDto getOrders(String userId) {
        return new OrdersDto(orderRepository.getOrders(new UserId(userId)).stream()
                .map(it -> new OrderDto(
                        it.getId().value(),
                        it.getClientOrderId().value(),
                        it.getUserId().value(),
                        it.getSide(),
                        it.getPrice().amount(),
                        it.getQuantity().value(),
                        it.getAsset(),
                        it.getRemainingQuantity().value(),
                        it.getStatus())
                ).toList());
    }

}
