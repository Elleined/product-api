package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularSellerProxyTest {

    @Mock
    private  SellerService sellerService;
    @Mock
    private  WholeSaleProductService wholeSaleProductService;
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
    void testSaleProduct() {
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
    void testSaveProduct() {
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
    void testUpdateProduct() {
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
    void testDeleteProduct() {
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
    void testAcceptOrder() {
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
    void testRejectOrder() {
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
    void testSoldOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void isExceedsToMaxListingPerDay() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void isExceedsToMaxRejectionPerDay() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void isExceedsToMaxAcceptedOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void isExceedsToMaxPendingOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }
}