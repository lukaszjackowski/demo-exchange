package com.example.demo.exchange;

import com.example.demo.exchange.domain.InMemoryJournal;
import com.example.demo.exchange.domain.InMemoryOrderRepository;
import com.example.demo.exchange.domain.eventprocessing.EngineEventProcessor;
import com.example.demo.exchange.domain.model.OrderStatus;
import com.example.demo.exchange.dto.CreateOrderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.example.demo.exchange.assertions.OrderDtoAssert.assertThat;
import static com.example.demo.exchange.domain.model.Asset.BTC_USD;
import static com.example.demo.exchange.domain.model.Side.BUY;
import static com.example.demo.exchange.domain.model.Side.SELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderMatchingTest {

    private final InMemoryJournal journal = new InMemoryJournal();
    private final InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    private final EngineEventProcessor engineEventProcessor = new ExchangeConfiguration().engineEventProcesesor(journal, orderRepository);
    private final ExchangeFacade exchangeFacade = new ExchangeConfiguration().exchangeFacade(orderRepository, engineEventProcessor);

    @Test
    void cannot_create_order_with_negative_price() {
        // given
        var createOrder = new CreateOrderDto("Bob", SELL, BTC_USD, BigDecimal.valueOf(-0.1), 500L,"IK-1");

        // when & then
        assertThatThrownBy(() -> {
            exchangeFacade.createOrder(createOrder);
        })
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Price cannot be negative: -0.1");
    }

    @Test
    void cannot_create_order_with_negative_quantity() {
        // given
        var createOrder = new CreateOrderDto("Bob", SELL, BTC_USD, BigDecimal.valueOf(0.1), -500L,"IK-1");

        // when & then
        assertThatThrownBy(() -> {
            exchangeFacade.createOrder(createOrder);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity cannot be negative: -500");
    }


    @Test
    void creates_one_order_without_a_match() throws InterruptedException {
        // given
        var latch = new CountDownLatch(1);
        orderRepository.setLatch(latch);
        var userBob = "Bob";
        var createOrder = new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 500L,"IK-1");

        // when
        exchangeFacade.createOrder(createOrder);

        // then
        var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();

        // and
        var result = exchangeFacade.getOrders(userBob).orders();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst())
                .hasClientOrderId(createOrder.clientOrderId())
                .hasUserId(userBob)
                .hasSide(createOrder.side())
                .hasAsset(createOrder.asset())
                .hasPrice(createOrder.price())
                .hasQuantity(createOrder.quantity())
                .hasRemainingQuantity(createOrder.quantity())
                .hasStatus(OrderStatus.NEW);
    }

    @Test
    void creates_three_orders_without_a_match() throws InterruptedException {
        // given
        var latch = new CountDownLatch(3);
        orderRepository.setLatch(latch);
        var userBob = "Bob";

        // when
        exchangeFacade.createOrder(new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 500L,"IK-1"));
        exchangeFacade.createOrder(new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 500L,"IK-2"));
        exchangeFacade.createOrder(new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 500L,"IK-3"));

        // then
        var completed = latch.await(2, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        var result = exchangeFacade.getOrders(userBob).orders();
        assertThat(result).hasSize(3);
    }

    @Test
    void matches_two_orders() throws InterruptedException {
        // given
        var latch = new CountDownLatch(2);
        orderRepository.setLatch(latch);
        var userBob = "Bob";
        var userAlice = "Alice";

        var bobOrder = new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 1000L, "IK-1");
        var aliceOrder = new CreateOrderDto(userAlice, BUY, BTC_USD, BigDecimal.valueOf(0.1), 500L, "IK-2");

        // when
        exchangeFacade.createOrder(bobOrder);
        exchangeFacade.createOrder(aliceOrder);


        // then
        var completed = latch.await(2, TimeUnit.SECONDS);
        assertThat(completed).isTrue();

        var bobResults = exchangeFacade.getOrders(userBob).orders();
        var aliceResults = exchangeFacade.getOrders(userAlice).orders();

        assertThat(bobResults).hasSize(1);
        assertThat(bobResults.getFirst())
                .hasClientOrderId(bobOrder.clientOrderId())
                .hasUserId(bobOrder.userId())
                .hasSide(bobOrder.side())
                .hasAsset(bobOrder.asset())
                .hasPrice(bobOrder.price())
                .hasQuantity(bobOrder.quantity())
                .hasRemainingQuantity(bobOrder.quantity() - aliceOrder.quantity())
                .hasStatus(OrderStatus.PARTIALLY_FILLED);

        assertThat(aliceResults).hasSize(1);
        assertThat(aliceResults.getFirst())
                .hasClientOrderId(aliceOrder.clientOrderId())
                .hasUserId(aliceOrder.userId())
                .hasSide(aliceOrder.side())
                .hasAsset(aliceOrder.asset())
                .hasPrice(aliceOrder.price())
                .hasQuantity(aliceOrder.quantity())
                .hasRemainingQuantity(0)
                .hasStatus(OrderStatus.FILLED);
    }

    @Test
    void matches_three_orders() throws InterruptedException {
        // given
        var latch = new CountDownLatch(3);
        orderRepository.setLatch(latch);
        var userBob = "Bob";
        var userAlice = "Alice";
        var userDylan = "Dylan";

        // when
        exchangeFacade.createOrder(new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 1000L, "IK-1"));
        exchangeFacade.createOrder(new CreateOrderDto(userAlice, BUY, BTC_USD, BigDecimal.valueOf(0.1), 500L, "IK-2"));
        exchangeFacade.createOrder(new CreateOrderDto(userDylan, BUY, BTC_USD, BigDecimal.valueOf(0.1), 500L, "IK-3"));


        // then
        var completed = latch.await(2, TimeUnit.SECONDS);
        assertThat(completed).isTrue();

        var bobResults = exchangeFacade.getOrders(userBob).orders();
        var aliceResults = exchangeFacade.getOrders(userAlice).orders();
        var dylanResults = exchangeFacade.getOrders(userDylan).orders();

        assertThat(bobResults).hasSize(1);
        assertThat(bobResults.getFirst().status()).isEqualTo(OrderStatus.FILLED);

        assertThat(aliceResults).hasSize(1);
        assertThat(aliceResults.getFirst().status()).isEqualTo(OrderStatus.FILLED);

        assertThat(dylanResults).hasSize(1);
        assertThat(dylanResults.getFirst().status()).isEqualTo(OrderStatus.FILLED);
    }

    @Test
    void does_not_process_the_same_entry_twice() throws InterruptedException {
        // given
        var latch = new CountDownLatch(1);
        orderRepository.setLatch(latch);
        var userBob = "Bob";
        exchangeFacade.createOrder(new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 500L,"IK-1"));

        // when
        exchangeFacade.createOrder(new CreateOrderDto(userBob, SELL, BTC_USD, BigDecimal.valueOf(0.1), 500L,"IK-1"));

        // then
        var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        var result = exchangeFacade.getOrders(userBob).orders();
        assertThat(result).hasSize(1);
    }

}
