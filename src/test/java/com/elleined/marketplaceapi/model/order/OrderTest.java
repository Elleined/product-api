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

    @Test
    void hasStatusOf() {
//        Order cancelledOrder = RetailOrder.retailOrderBuilder()
//                .status(Order.Status.CANCELLED)
//                .build();
//        Order soldOrder = RetailOrder.retailOrderBuilder()
//                .status(Order.Status.SOLD)
//                .build();
        Order pendingOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.PENDING)
                .build();
        Order acceptedOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.ACCEPTED)
                .build();
        Order rejectedOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.REJECTED)
                .build();

        assertTrue(acceptedOrder.isAccepted());
        assertTrue(pendingOrder.isPending());
        assertTrue(rejectedOrder.isRejected());
    }
}