package com.example.demo.exchange.assertions;

import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.OrderStatus;
import com.example.demo.exchange.domain.model.Side;
import com.example.demo.exchange.dto.OrderDto;
import org.assertj.core.api.AbstractAssert;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderDtoAssert extends AbstractAssert<OrderDtoAssert, OrderDto> {

    private static final String MESSAGE_PATTERN = "Expected %d, but was %d";

    protected OrderDtoAssert(OrderDto actual) {
        super(actual, OrderDtoAssert.class);
    }

    public static OrderDtoAssert assertThat(OrderDto actual) {
        return new OrderDtoAssert(actual);
    }

    public OrderDtoAssert hasClientOrderId(String expectedId) {
        isNotNull();
        if (!Objects.equals(actual.clientOrderId(), expectedId)) {
            failWithMessage(MESSAGE_PATTERN, expectedId, actual.clientOrderId());
        }
        return this;
    }

    public OrderDtoAssert hasUserId(String expectedId) {
        isNotNull();
        if (!Objects.equals(actual.userId(), expectedId)) {
            failWithMessage(MESSAGE_PATTERN, expectedId, actual.userId());
        }
        return this;
    }

    public OrderDtoAssert hasPrice(BigDecimal price) {
        isNotNull();
        if (actual.price().compareTo(price) != 0) {
            failWithMessage(MESSAGE_PATTERN, price, actual.price());
        }
        return this;
    }

    public OrderDtoAssert hasSide(Side side) {
        isNotNull();
        if (actual.side() != side) {
            failWithMessage(MESSAGE_PATTERN, actual.side());
        }
        return this;
    }

    public OrderDtoAssert hasStatus(OrderStatus status) {
        isNotNull();
        if (actual.status() != status) {
            failWithMessage(MESSAGE_PATTERN, actual.status());
        }
        return this;
    }

    public OrderDtoAssert hasRemainingQuantity(long remainingQuantity) {
        isNotNull();
        if (actual.remainingQuantity() != remainingQuantity) {
            failWithMessage(MESSAGE_PATTERN, remainingQuantity, actual.remainingQuantity());
        }
        return this;
    }

    public OrderDtoAssert hasQuantity(long quantity) {
        isNotNull();
        if (actual.quantity() != quantity) {
            failWithMessage(MESSAGE_PATTERN, quantity, actual.quantity());
        }
        return this;
    }

    public OrderDtoAssert hasAsset(Asset asset) {
        isNotNull();
        if (actual.asset() != asset) {
            failWithMessage(MESSAGE_PATTERN, asset, actual.asset());
        }
        return this;
    }


}
