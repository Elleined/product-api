package com.elleined.marketplaceapi.service.user.buyer.regular;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerMaxOrderPerDayException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import com.elleined.marketplaceapi.service.user.buyer.BuyerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularBuyerProxyTest {

    @Mock
    private BuyerServiceImpl buyerService;

    @InjectMocks
    private RegularBuyerProxy regularBuyerProxy;

    @Test
    void order() {
        // Expected/ Actual values

        // Mock data
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .build();

        // Stubbing methods
        when(buyerService.order(any(User.class), any(RetailOrderDTO.class))).thenReturn(new RetailOrder());

        // Calling the method
        regularBuyerProxy.order(user, new RetailOrderDTO());

        // Assertions

        // Behavior verification
        verify(buyerService).order(any(User.class), any(RetailOrderDTO.class));
    }

    @Test
    void wholeSaleOrder() {
        // Expected/ Actual values

        // Mock data
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .build();

        // Stubbing methods
        when(buyerService.order(any(User.class), any(WholeSaleOrderDTO.class))).thenReturn(new WholeSaleOrder());

        // Calling the method
        regularBuyerProxy.order(user, new WholeSaleOrderDTO());

        // Assertions

        // Behavior verification
        verify(buyerService).order(any(User.class), any(WholeSaleOrderDTO.class));
    }

    @Test
    @DisplayName("order validations 1: cannot order if there are more than 10 pending/ created order within this day")
    void cannotOrderIfThereAre10PendingOrderWithinThisDay() {
        // Mock data
        List<RetailOrder> retailOrders = Arrays.asList(
                getRetailOrder(),
                getRetailOrder(),
                getRetailOrder(),
                getRetailOrder(),
                getRetailOrder()
        );
        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(
                getWholeSaleOrder(),
                getWholeSaleOrder(),
                getWholeSaleOrder(),
                getWholeSaleOrder(),
                getWholeSaleOrder()
        );

        User user = new User();
        user.setRetailOrders(retailOrders);
        user.setWholeSaleOrders(wholeSaleOrders);

        assertThrowsExactly(BuyerMaxOrderPerDayException.class, () -> regularBuyerProxy.order(user, new RetailOrderDTO()));
        assertThrowsExactly(BuyerMaxOrderPerDayException.class, () -> regularBuyerProxy.order(user, new WholeSaleOrderDTO()));

        verifyNoInteractions(buyerService);
    }

    @Test
    void cancelOrder() {
        // Expected/ Actual values

        // Mock data

        // Stubbing methods
        doNothing().when(buyerService).cancelOrder(any(User.class), any(RetailOrder.class));

        // Calling the method
        regularBuyerProxy.cancelOrder(new User(), new RetailOrder());

        // Assertions

        // Behavior verification
        verify(buyerService).cancelOrder(any(User.class), any(RetailOrder.class));
    }

    @Test
    void wholeSaleCancelOrder() {
// Expected/ Actual values

        // Mock data

        // Stubbing methods
        doNothing().when(buyerService).cancelOrder(any(User.class), any(WholeSaleOrder.class));

        // Calling the method
        regularBuyerProxy.cancelOrder(new User(), new WholeSaleOrder());

        // Assertions

        // Behavior verification
        verify(buyerService).cancelOrder(any(User.class), any(WholeSaleOrder.class));
    }

    @Test
    void isBuyerExceedsToMaxOrderPerDay() {
        // Expected/ Actual values

        List<RetailOrder> retailOrders = Arrays.asList(
                getRetailOrder(),
                getRetailOrder(),
                getRetailOrder(),
                getRetailOrder(),
                getRetailOrder()
        );
        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(
                getWholeSaleOrder(),
                getWholeSaleOrder(),
                getWholeSaleOrder(),
                getWholeSaleOrder(),
                getWholeSaleOrder()
        );

        // Mock data
        User user = new User();
        user.setWholeSaleOrders(wholeSaleOrders);
        user.setRetailOrders(retailOrders);

        // Stubbing methods

        // Calling the method
        assertTrue(regularBuyerProxy.isBuyerExceedsToMaxOrderPerDay(user));

        // Assertions

        // Behavior verification
    }

    public RetailOrder getRetailOrder() {
        return RetailOrder.retailOrderBuilder()
                .status(Order.Status.PENDING)
                .orderDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public WholeSaleOrder getWholeSaleOrder() {
        return WholeSaleOrder.wholeSaleOrderBuilder()
                .status(Order.Status.PENDING)
                .orderDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}