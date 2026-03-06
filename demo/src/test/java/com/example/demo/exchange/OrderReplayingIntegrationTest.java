package com.example.demo.exchange;


import com.example.demo.TestcontainersConfiguration;
import com.example.demo.exchange.domain.eventprocessing.EngineEventProcessor;
import com.example.demo.exchange.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.example.demo.exchange.assertions.OrderDtoAssert.assertThat;
import static com.example.demo.exchange.domain.model.Asset.BTC_USD;
import static com.example.demo.exchange.domain.model.Side.BUY;
import static com.example.demo.exchange.domain.model.Side.SELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Sql("/replaying-test-data.sql")
public class OrderReplayingIntegrationTest {

    @Autowired
    EngineEventProcessor engineEventProcessor;
    @Autowired
    ExchangeFacade exchangeFacade;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void replays_data_successfully() throws InterruptedException {
        // given
        var userBob =  "userBob";
        var userAlice =  "userAlice";

        // when
        engineEventProcessor.replayFromJournal();

        // then
        await()
                .atMost(1, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(50))
                .untilAsserted(() -> {

                    var bobOrders = exchangeFacade.getOrders(userBob).orders();
                    assertThat(bobOrders).hasSize(1);
                    assertThat(bobOrders.getFirst())
                            .hasUserId(userBob)
                            .hasSide(SELL)
                            .hasAsset(BTC_USD)
                            .hasPrice(BigDecimal.valueOf(0.500))
                            .hasQuantity(500)
                            .hasRemainingQuantity(400)
                            .hasStatus(OrderStatus.PARTIALLY_FILLED);

                    // when
                    var aliceOrders = exchangeFacade.getOrders(userAlice).orders();

                    // then
                    assertThat(aliceOrders).hasSize(1);
                    assertThat(aliceOrders.getFirst())
                            .hasUserId(userAlice)
                            .hasSide(BUY)
                            .hasAsset(BTC_USD)
                            .hasPrice(BigDecimal.valueOf(0.500))
                            .hasQuantity(100)
                            .hasRemainingQuantity(0)
                            .hasStatus(OrderStatus.FILLED);

                });

    }
}
