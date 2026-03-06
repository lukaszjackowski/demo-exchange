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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class OrderProcessingErrorHandlingTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void handle_negative_price_error() {
        // given
        var requestBody = """
                {
                    "userId": "bob-123",
                    "side": "SELL",
                    "asset": "BTC_USD",
                    "price": -0.1,
                    "quantity": 500,
                    "clientOrderId": "IK-1"
                }
                """;

        // when
        var postResponse = createOrderWithResponse(requestBody);

        // then
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    @Test
    public void handle_negative_quantity_error() {
        // given
        var requestBody = """
                {
                    "userId": "bob-123",
                    "side": "SELL",
                    "asset": "BTC_USD",
                    "price": 0.1,
                    "quantity": -500,
                    "clientOrderId": "IK-1"
                }
                """;

        // when
        var postResponse = createOrderWithResponse(requestBody);

        // then
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    private ResponseEntity<Void> createOrderWithResponse(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForEntity("/api/v1/orders", request, Void.class);
    }

}
