package com.elleined.marketplaceapi.service.user.buyer.premium;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.buyer.BuyerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumBuyerProxyTest {

    @Mock
    private BuyerServiceImpl buyerService;
    @InjectMocks
    private PremiumBuyerProxy premiumBuyerProxy;


    @Test
    void order() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods
        when(buyerService.order(any(User.class), any(RetailOrderDTO.class))).thenReturn(new RetailOrder());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> premiumBuyerProxy.order(new User(), new RetailOrderDTO()));

        // Behavior Verifications
        verify(buyerService).order(any(User.class), any(RetailOrderDTO.class));
    }

    @Test
    void wholeSaleOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods
        when(buyerService.order(any(User.class), any(WholeSaleOrderDTO.class))).thenReturn(new WholeSaleOrder());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> premiumBuyerProxy.order(new User(), new WholeSaleOrderDTO()));

        // Behavior Verifications
        verify(buyerService).order(any(User.class), any(WholeSaleOrderDTO.class));
    }

    @Test
    void cancelOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods
        doNothing().when(buyerService).cancelOrder(any(User.class), any(RetailOrder.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> premiumBuyerProxy.cancelOrder(new User(), new RetailOrder()));

        // Behavior Verifications
        verify(buyerService).cancelOrder(any(User.class), any(RetailOrder.class));
    }

    @Test
    void testCancelOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods
        doNothing().when(buyerService).cancelOrder(any(User.class), any(WholeSaleOrder.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> premiumBuyerProxy.cancelOrder(new User(), new WholeSaleOrder()));

        // Behavior Verifications
        verify(buyerService).cancelOrder(any(User.class), any(WholeSaleOrder.class));
    }
}