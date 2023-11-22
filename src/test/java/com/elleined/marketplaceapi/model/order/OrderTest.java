package com.elleined.marketplaceapi.model.order;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void reachedCancellingTimeLimit() {
        Order order = RetailOrder.retailOrderBuilder()
                .updatedAt(LocalDateTime.now().minusDays(1)) // Kahapon inorder kaya ngayon hnd na pede i cancel
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