package com.elleined.marketplaceapi.model.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void reachedCancellingTimeLimit() {
        Order order = RetailOrder.retailOrderBuilder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        assertTrue(order.reachedCancellingTimeLimit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    void status(String expectedOrderStatus) {
        Order actual = RetailOrder.retailOrderBuilder()
                .status(Order.Status.valueOf(expectedOrderStatus))
                .build();

        assertEquals(expectedOrderStatus, actual.getStatus().name());
    }
}