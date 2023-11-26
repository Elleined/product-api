package com.elleined.marketplaceapi.service.user.seller.fee;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PremiumSellerFeeServiceTest {

    @Test
    void getListingFee() {
        // Expected values
        double expected = 0;
        // Mock Data
        SellerFeeService sellerFeeService = new PremiumSellerFeeService();
        double productTotalPrice = 100;
        // Stubbing methods

        // Calling the method
        double actual = sellerFeeService.getListingFee(productTotalPrice);

        // Assestions
        assertEquals(expected, actual);

        // Behavior verification
    }

    @Test
    void getSuccessfulTransactionFee() {
        // Expected values
        double expected = 0.9999999776482582;
        // Mock Data
        SellerFeeService sellerFeeService = new PremiumSellerFeeService();
        double productTotalPrice = 100;
        // Stubbing methods

        // Calling the method
        double actual = sellerFeeService.getSuccessfulTransactionFee(productTotalPrice);

        // Assestions
        assertEquals(expected, actual, "Failing because the expected is not the " + PremiumSellerFeeService.SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE + " of successful transaction fee of total price.");

        // Behavior verification
    }
}