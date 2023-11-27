package com.elleined.marketplaceapi.service.user.seller.premium;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import com.elleined.marketplaceapi.service.user.seller.fee.SellerFeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class PremiumSellerProxyTest {

    @Mock
    private SellerFeeService sellerFeeService;
    @Mock
    private SellerService sellerService;
    @Mock
    private FeeService feeService;
    @Mock
    private ATMValidator atmValidator;
    @InjectMocks
    private PremiumSellerProxy premiumSellerProxy;
    

    @Test
    void saleProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleSaleProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void saveProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleSaveProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void updateProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleUpdateProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void deleteProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleDeleteProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void acceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleAcceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void rejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleRejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void soldOrder() {
        // Expected values
        double expectedSuccessfulTransactionFee = 10;
        BigDecimal expectedUserBalance = new BigDecimal(490);

        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setPrice(50);
        // Stubbing methods
        when(sellerFeeService.getSuccessfulTransactionFee(anyDouble())).thenReturn(expectedSuccessfulTransactionFee);
        when(atmValidator.isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class))).thenReturn(false);
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedSuccessfulTransactionFee)));
            return user;
        }).when(feeService).deductSuccessfulTransactionFee(any(User.class), anyDouble());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.soldOrder(user, retailOrder));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(sellerFeeService).getSuccessfulTransactionFee(anyDouble());
        verify(atmValidator).isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class));
        verify(feeService).deductSuccessfulTransactionFee(any(User.class), anyDouble());
        verify(sellerService).soldOrder(any(User.class), any(RetailOrder.class));
    }

    @Test
    void wholeSaleSoldOrder() {
        // Expected values
        double expectedSuccessfulTransactionFee = 10;
        BigDecimal expectedUserBalance = new BigDecimal(490);

        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        WholeSaleOrder wholeSaleOrder = new WholeSaleOrder();
        wholeSaleOrder.setPrice(50);
        // Stubbing methods
        when(sellerFeeService.getSuccessfulTransactionFee(anyDouble())).thenReturn(expectedSuccessfulTransactionFee);
        when(atmValidator.isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class))).thenReturn(false);
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedSuccessfulTransactionFee)));
            return user;
        }).when(feeService).deductSuccessfulTransactionFee(any(User.class), anyDouble());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.soldOrder(user, wholeSaleOrder));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(sellerFeeService).getSuccessfulTransactionFee(anyDouble());
        verify(atmValidator).isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class));
        verify(feeService).deductSuccessfulTransactionFee(any(User.class), anyDouble());
        verify(sellerService).soldOrder(any(User.class), any(WholeSaleOrder.class));
    }
}