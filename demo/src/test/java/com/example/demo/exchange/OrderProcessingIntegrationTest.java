package com.example.demo.exchange;

import com.example.demo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class OrderProcessingIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void creates_two_orders_and_match_them_together() {
        // given
        var sellRequestBody = """
                {
                    "userId": "bob-123",
                    "side": "SELL",
                    "asset": "BTC_USD",
                    "price": 0.1,
                    "quantity": 500,
                    "clientOrderId": "IK-1"
                }
                """;

        var buyRequestBody = """
                {
                    "userId": "alice-456",
                    "side": "BUY",
                    "asset": "BTC_USD",
                    "price": 0.2,
                    "quantity": 300,
                    "clientOrderId": "IK-2"
                }
                """;

        // when
        createOrder(sellRequestBody);
        createOrder(buyRequestBody);

        // then
        var expectedBobResponse = """
                {
                    "orders": [
                        {
                            "clientOrderId": "IK-1",
                            "userId": "bob-123",
                            "side": "SELL",
                            "price": 0.1,
                            "asset": "BTC_USD",
                            "quantity": 500,
                            "remainingQuantity": 200,
                            "status": "PARTIALLY_FILLED"
                        }
                    ]
                }
                """;

        var expectedAliceResponse = """
                {
                    "orders": [
                        {
                            "clientOrderId": "IK-2",
                            "userId": "alice-456",
                            "side": "BUY",
                            "price": 0.2,
                            "asset": "BTC_USD",
                            "quantity": 300,
                            "remainingQuantity": 0,
                            "status": "FILLED"
                        }
                    ]
                }
                """;

        await()
                .atMost(1, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(50))
                .untilAsserted(() -> {
                    ResponseEntity<String> bobResponse = restTemplate.getForEntity(
                            "/api/v1/orders?user-id={userId}",
                            String.class,
                            "bob-123"
                    );
                    ResponseEntity<String> aliceResponse = restTemplate.getForEntity(
                            "/api/v1/orders?user-id={userId}",
                            String.class,
                            "alice-456"
                    );

                    assertThat(bobResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
                    assertEquals(expectedBobResponse, bobResponse.getBody(), LENIENT);

                    assertThat(aliceResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
                    assertEquals(expectedAliceResponse, aliceResponse.getBody(), LENIENT);
                });
    }

    private void createOrder(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        var postResponse = restTemplate.postForEntity("/api/v1/orders", request, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(202));
    }

}
