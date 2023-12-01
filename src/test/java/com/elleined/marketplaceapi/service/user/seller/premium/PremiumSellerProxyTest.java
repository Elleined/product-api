package com.elleined.marketplaceapi.service.user.seller.premium;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.dto.product.sale.SaleRetailProductRequest;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    void saveProduct() throws IOException {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(sellerService.saveProduct(any(User.class), any(RetailProductDTO.class), any(MultipartFile.class))).thenReturn(new RetailProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.saveProduct(new User(), new RetailProductDTO(), MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verify(sellerService).saveProduct(any(User.class), any(RetailProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void wholeSaleSaveProduct() throws IOException {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(sellerService.saveProduct(any(User.class), any(WholeSaleProductDTO.class), any(MultipartFile.class))).thenReturn(new WholeSaleProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.saveProduct(new User(), new WholeSaleProductDTO(), MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verify(sellerService).saveProduct(any(User.class), any(WholeSaleProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void updateProduct() throws IOException {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(sellerService.updateProduct(any(User.class), any(RetailProduct.class), any(RetailProductDTO.class), any(MultipartFile.class))).thenReturn(new RetailProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.updateProduct(new User(), new RetailProduct(), new RetailProductDTO(), MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verify(sellerService).updateProduct(any(User.class), any(RetailProduct.class), any(RetailProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void wholeSaleUpdateProduct() throws IOException {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(sellerService.updateProduct(any(User.class), any(WholeSaleProduct.class), any(WholeSaleProductDTO.class), any(MultipartFile.class))).thenReturn(new WholeSaleProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.updateProduct(new User(), new WholeSaleProduct(), new WholeSaleProductDTO(), MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verify(sellerService).updateProduct(any(User.class), any(WholeSaleProduct.class), any(WholeSaleProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void deleteProduct() throws IOException {
// Expected values

        // Mock Data

        // Stubbing methods
        doNothing().when(sellerService).deleteProduct(any(User.class), any(RetailProduct.class));

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.deleteProduct(new User(), new RetailProduct()));

        // Behavior verification
        verify(sellerService).deleteProduct(any(User.class), any(RetailProduct.class));
    }

    @Test
    void wholeSaleDeleteProduct() {
// Expected values

        // Mock Data

        // Stubbing methods
        doNothing().when(sellerService).deleteProduct(any(User.class), any(WholeSaleProduct.class));

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.deleteProduct(new User(), new WholeSaleProduct()));

        // Behavior verification
        verify(sellerService).deleteProduct(any(User.class), any(WholeSaleProduct.class));
    }

    @Test
    void acceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        doNothing().when(sellerService).acceptOrder(any(User.class), any(RetailOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.acceptOrder(new User(), new RetailOrder(), ""));

        // Behavior verification
        verify(sellerService).acceptOrder(any(User.class), any(RetailOrder.class), anyString());
    }

    @Test
    void wholeSaleAcceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        doNothing().when(sellerService).acceptOrder(any(User.class), any(WholeSaleOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.acceptOrder(new User(), new WholeSaleOrder(), ""));

        // Behavior verification
        verify(sellerService).acceptOrder(any(User.class), any(WholeSaleOrder.class), anyString());
    }

    @Test
    void rejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        doNothing().when(sellerService).rejectOrder(any(User.class), any(RetailOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.rejectOrder(new User(), new RetailOrder(), ""));

        // Behavior verification
        verify(sellerService).rejectOrder(any(User.class), any(RetailOrder.class), anyString());
    }

    @Test
    void wholeSaleRejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        doNothing().when(sellerService).rejectOrder(any(User.class), any(WholeSaleOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> premiumSellerProxy.rejectOrder(new User(), new WholeSaleOrder(), ""));

        // Behavior verification
        verify(sellerService).rejectOrder(any(User.class), any(WholeSaleOrder.class), anyString());
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