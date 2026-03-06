package com.example.demo.exchange.adapters.api;

import com.example.demo.exchange.ExchangeFacade;
import com.example.demo.exchange.dto.CreateOrderDto;
import com.example.demo.exchange.dto.OrdersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderEndpoint {

    private final ExchangeFacade exchangeFacade;

    public OrderEndpoint(ExchangeFacade exchangeFacade) {
        this.exchangeFacade = exchangeFacade;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody CreateOrderDto request) {
        exchangeFacade.createOrder(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<OrdersDto> getOrders(@RequestParam(name = "user-id") String userId) {
        var orders = exchangeFacade.getOrders(userId);
        return ResponseEntity.ok(orders);
    }

}
