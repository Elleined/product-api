package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
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

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleSoldOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

}