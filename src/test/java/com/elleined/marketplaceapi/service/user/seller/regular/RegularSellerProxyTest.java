package com.elleined.marketplaceapi.service.user.seller.regular;

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
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularSellerProxyTest {

    @Mock
    private  SellerService sellerService;
    @Mock
    private SellerFeeService sellerFeeService;
    @Mock
    private RegularSellerRestriction regularSellerRestriction;
    @Mock
    private ATMValidator atmValidator;
    @Mock
    private  RetailProductService retailProductService;
    @Mock
    private  FeeService feeService;
    @InjectMocks
    private RegularSellerProxy regularSellerProxy;

    @Test
    void saleProduct() {
        // Expected values
        double expectedTotalPrice = 900;
        double expectedListingFee = 90;
        BigDecimal expectedUserBalance = new BigDecimal(10);

        // Mock Data
        User seller = spy(User.class);
        seller.setBalance(new BigDecimal(100));
        // Stubbing methods
        doReturn(false).when(seller).isBalanceNotEnough(any());

        when(retailProductService.calculateTotalPrice(any(RetailProduct.class), any(SaleRetailProductRequest.class))).thenReturn(expectedTotalPrice);
        when(sellerFeeService.getListingFee(anyDouble())).thenReturn(expectedListingFee);

        doAnswer(i -> {
            seller.setBalance(seller.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return seller;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.saleProduct(seller, new RetailProduct(), new SaleRetailProductRequest()));
        assertEquals(expectedUserBalance, seller.getBalance());

        // Behavior verification
        verify(retailProductService).calculateTotalPrice(any(RetailProduct.class), any(SaleRetailProductRequest.class));
        verify(sellerFeeService).getListingFee(anyDouble());
        verify(feeService).deductListingFee(any(User.class), anyDouble());
    }

    @Test
    void wholeSaleSaleProduct() {
        // Expected values
        double expectedListingFee = 90;
        BigDecimal expectedUserBalance = new BigDecimal(10);

        // Mock Data
        User seller = spy(User.class);
        seller.setBalance(new BigDecimal(100));
        // Stubbing methods

        doReturn(false).when(seller).isBalanceNotEnough(any());
        
        when(sellerFeeService.getListingFee(anyDouble())).thenReturn(expectedListingFee);

        doAnswer(i -> {
            seller.setBalance(seller.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return seller;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.saleProduct(seller, new RetailProduct(), new SaleRetailProductRequest()));
        assertEquals(expectedUserBalance, seller.getBalance());

        // Behavior verification
        verify(sellerFeeService).getListingFee(anyDouble());
        verify(feeService).deductListingFee(any(User.class), anyDouble());
    }

    @Test
    void saveProduct() throws IOException {
        // Expected values
        double expectedTotalPrice = 10;
        double expectedListingFee = 10;

        BigDecimal expectedUserBalance = new BigDecimal(490);
        // Mock Data
        User user = spy(User.class);
        user.setBalance(new BigDecimal(500));

        RetailProductDTO retailProductDTO = RetailProductDTO.retailProductDTOBuilder()
                .pricePerUnit(1)
                .quantityPerUnit(1)
                .availableQuantity(1)
                .build();

        // Stubbing methods
        doReturn(false).when(user).isBalanceNotEnough(any());

        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxListingPerDay(any(User.class))).thenReturn(false);
        when(retailProductService.calculateTotalPrice(any(Double.class), any(Integer.class), any(Integer.class))).thenReturn(expectedTotalPrice);
        when(sellerFeeService.getListingFee(anyDouble())).thenReturn(expectedListingFee);
        when(atmValidator.isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class))).thenReturn(false);
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return user;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());
        when(sellerService.saveProduct(any(User.class), any(RetailProductDTO.class), any(MultipartFile.class))).thenReturn(new RetailProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.saveProduct(user, retailProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxListingPerDay(any(User.class));
        verify(sellerFeeService).getListingFee(anyDouble());
        verify(atmValidator).isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class));
        verify(feeService).deductListingFee(any(User.class), anyDouble());
        verify(sellerService).saveProduct(any(User.class), any(RetailProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void wholeSaleSaveProduct() throws IOException {
        // Expected values
        double expectedListingFee = 10;
        BigDecimal expectedUserBalance = new BigDecimal(490);

        // Mock Data
        User user = spy(User.class);
        user.setBalance(new BigDecimal(500));

        WholeSaleProductDTO wholeSaleProductDTO = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .totalPrice(500)
                .build();

        // Stubbing methods
        doReturn(false).when(user).isBalanceNotEnough(any());
        when(sellerFeeService.getListingFee(anyDouble())).thenReturn(expectedListingFee);
        when(atmValidator.isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class))).thenReturn(false);
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return user;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());
        when(sellerService.saveProduct(any(User.class), any(WholeSaleProductDTO.class), any(MultipartFile.class))).thenReturn(new WholeSaleProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.saveProduct(user, wholeSaleProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(sellerFeeService).getListingFee(anyDouble());
        verify(atmValidator).isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class));
        verify(feeService).deductListingFee(any(User.class), anyDouble());
        verify(sellerService).saveProduct(any(User.class), any(WholeSaleProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void updateProduct() throws IOException {
        // Expected values
        double expectedTotalPrice = 10;
        double expectedListingFee = 5;

        BigDecimal expectedUserBalance = new BigDecimal(495);
        // Mock Data
        User user = spy(User.class);
        user.setBalance(new BigDecimal(500));

        RetailProductDTO retailProductDTO = RetailProductDTO.retailProductDTOBuilder()

                .build();
        // Stubbing methods
        doReturn(false).when(user).isBalanceNotEnough(any());

        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        when(retailProductService.calculateTotalPrice(anyDouble(), anyInt(), anyInt())).thenReturn(expectedTotalPrice);
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return user;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());
        when(sellerService.updateProduct(any(User.class), any(RetailProduct.class), any(RetailProductDTO.class), any(MultipartFile.class))).thenReturn(new RetailProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.updateProduct(user, new RetailProduct(), retailProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(retailProductService).calculateTotalPrice(anyDouble(), anyInt(), anyInt());
        verify(feeService).deductListingFee(any(User.class), anyDouble());
        verify(sellerService).updateProduct(any(User.class), any(RetailProduct.class), any(RetailProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void wholeSaleUpdateProduct() throws IOException {
        // Expected values
        double expectedTotalPrice = 10;
        double expectedListingFee = 5;

        BigDecimal expectedUserBalance = new BigDecimal(495);
        // Mock Data
        User user = spy(User.class);
        user.setBalance(new BigDecimal(500));

        WholeSaleProductDTO wholeSaleProductDTO = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .totalPrice(500)
                .build();

        // Stubbing methods
        doReturn(false).when(user).isBalanceNotEnough(any());

        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return user;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());
        when(sellerService.updateProduct(any(User.class), any(WholeSaleProduct.class), any(WholeSaleProductDTO.class), any(MultipartFile.class))).thenReturn(new WholeSaleProduct());
        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.updateProduct(user, new WholeSaleProduct(), wholeSaleProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(feeService).deductListingFee(any(User.class), anyDouble());
        verify(sellerService).updateProduct(any(User.class), any(WholeSaleProduct.class), any(WholeSaleProductDTO.class), any(MultipartFile.class));
    }

    @Test
    void deleteProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        doNothing().when(sellerService).deleteProduct(any(User.class), any(RetailProduct.class));

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.deleteProduct(new User(), new RetailProduct()));

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(sellerService).deleteProduct(any(User.class), any(RetailProduct.class));
    }


    @Test
    void wholeSaleDeleteProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        doNothing().when(sellerService).deleteProduct(any(User.class), any(WholeSaleProduct.class));

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.deleteProduct(new User(), new WholeSaleProduct()));

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(sellerService).deleteProduct(any(User.class), any(WholeSaleProduct.class));
    }

    @Test
    void acceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        doNothing().when(sellerService).acceptOrder(any(User.class), any(RetailOrder.class), anyString());
        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.acceptOrder(new User(), new RetailOrder(), ""));

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(sellerService).acceptOrder(any(User.class), any(RetailOrder.class), anyString());
    }

    @Test
    void wholeSaleAcceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(regularSellerRestriction.isExceedsToMaxAcceptedOrder(any(User.class))).thenReturn(false);
        doNothing().when(sellerService).acceptOrder(any(User.class), any(WholeSaleOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.acceptOrder(new User(), new WholeSaleOrder(), ""));

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxAcceptedOrder(any(User.class));
        verify(sellerService).acceptOrder(any(User.class), any(WholeSaleOrder.class), anyString());
    }

    @Test
    void rejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxRejectionPerDay(any(User.class))).thenReturn(false);
        doNothing().when(sellerService).rejectOrder(any(User.class), any(RetailOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.rejectOrder(new User(), new RetailOrder(), ""));

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxRejectionPerDay(any(User.class));
        verify(sellerService).rejectOrder(any(User.class), any(RetailOrder.class), anyString());
    }

    @Test
    void wholeSaleRejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods
        when(regularSellerRestriction.isExceedsToMaxPendingOrder(any(User.class))).thenReturn(false);
        when(regularSellerRestriction.isExceedsToMaxRejectionPerDay(any(User.class))).thenReturn(false);
        doNothing().when(sellerService).rejectOrder(any(User.class), any(WholeSaleOrder.class), anyString());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.rejectOrder(new User(), new WholeSaleOrder(), ""));

        // Behavior verification
        verify(regularSellerRestriction).isExceedsToMaxPendingOrder(any(User.class));
        verify(regularSellerRestriction).isExceedsToMaxRejectionPerDay(any(User.class));
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
        assertDoesNotThrow(() -> regularSellerProxy.soldOrder(user, retailOrder));
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
        assertDoesNotThrow(() -> regularSellerProxy.soldOrder(user, wholeSaleOrder));
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(sellerFeeService).getSuccessfulTransactionFee(anyDouble());
        verify(atmValidator).isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class));
        verify(feeService).deductSuccessfulTransactionFee(any(User.class), anyDouble());
        verify(sellerService).soldOrder(any(User.class), any(WholeSaleOrder.class));
    }

}