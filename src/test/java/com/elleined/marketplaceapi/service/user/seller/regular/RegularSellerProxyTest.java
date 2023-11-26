package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularSellerProxyTest {

    @Mock
    private  SellerService sellerService;
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

        // Mock Data
        User user = spy(User.class);

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
        when(atmValidator.isUserTotalPendingRequestAmountAboveBalance(any(User.class), any(BigDecimal.class))).thenReturn(false);

        doAnswer(i -> {
            user.setBalance(user.getBalance().subtract(new BigDecimal(expectedListingFee)));
            return user;
        }).when(feeService).deductListingFee(any(User.class), anyDouble());
        when(sellerService.saveProduct(any(User.class), any(RetailProductDTO.class), any(MultipartFile.class))).thenReturn(new RetailProduct());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> regularSellerProxy.saveProduct(user, retailProductDTO, MultiPartFileDataFactory.notEmpty()));

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