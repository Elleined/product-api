package com.elleined.marketplaceapi.model.order;

import org.junit.jupiter.api.Test;

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

    @Test
    void isAccepted() {
    }

    @Test
    void isPending() {
    }

    @Test
    void isRejected() {
    }
}