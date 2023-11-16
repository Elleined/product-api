package com.elleined.marketplaceapi.model.product;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.elleined.marketplaceapi.model.product.Product.State.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    @Test
    void hasOrderStatusOf() {
        List<RetailOrder> retailOrders = Arrays.asList(
                RetailOrder.retailOrderBuilder()
                        .status(Order.Status.SOLD)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .status(Order.Status.PENDING)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .status(Order.Status.ACCEPTED)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .status(Order.Status.REJECTED)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .status(Order.Status.CANCELLED)
                        .build()
        );
        Product retailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(retailOrders)
                .build();


        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(
                WholeSaleOrder.wholeSaleOrderBuilder()
                        .status(Order.Status.SOLD)
                        .build(),
                WholeSaleOrder.wholeSaleOrderBuilder()
                        .status(Order.Status.PENDING)
                        .build(),
                WholeSaleOrder.wholeSaleOrderBuilder()
                        .status(Order.Status.ACCEPTED)
                        .build(),
                WholeSaleOrder.wholeSaleOrderBuilder()
                        .status(Order.Status.REJECTED)
                        .build(),
                WholeSaleOrder.wholeSaleOrderBuilder()
                        .status(Order.Status.CANCELLED)
                        .build()
        );
        Product wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(wholeSaleOrders)
                .build();

        assertTrue(retailProduct.hasPendingOrder());
        assertTrue(retailProduct.hasSoldOrder());
        assertTrue(retailProduct.hasAcceptedOrder());

        assertTrue(wholeSaleProduct.hasPendingOrder());
        assertTrue(wholeSaleProduct.hasSoldOrder());
        assertTrue(wholeSaleProduct.hasAcceptedOrder());
    }

    @Test
    void hasStateOf() {
        RetailProduct expiredProduct = RetailProduct.retailProductBuilder()
                .state(EXPIRED)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();

        Product listinProduct = RetailProduct.retailProductBuilder()
                .state(LISTING)
                .build();
        Product pendingProduct = RetailProduct.retailProductBuilder()
                .state(PENDING)
                .build();
        Product rejectedProduct = RetailProduct.retailProductBuilder()
                .state(REJECTED)
                .build();
        Product soldProduct = RetailProduct.retailProductBuilder()
                .state(SOLD)
                .build();

        assertTrue(expiredProduct.isExpired());
        assertTrue(rejectedProduct.isRejected());
        assertTrue(listinProduct.isListed());
        assertTrue(soldProduct.isSold());
        assertTrue(pendingProduct.isPending());
    }


    @Test
    void hasStatusOf() {
        Product activeProduct = RetailProduct.retailProductBuilder()
                .status(Product.Status.ACTIVE)
                .build();

        Product inActiveProduct = RetailProduct.retailProductBuilder()
                .status(Product.Status.INACTIVE)
                .build();

        assertTrue(activeProduct.isNotDeleted());
        assertTrue(inActiveProduct.isDeleted());
    }

    @Test
    void orderQuantityShouldNotExceedToProductAvailableQuantity() {
        Product actual = RetailProduct.retailProductBuilder()
                .availableQuantity(500)
                .build();

        int userOrderQuantity = 10;

        assertFalse(actual.isExceedingToAvailableQuantity(userOrderQuantity));
    }
}