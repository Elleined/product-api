package com.elleined.marketplaceapi.service.user.seller.fee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("premiumSellerFeeService")
public class PremiumSellerFeeService implements SellerFeeService {
    public static final float SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE = 1;

    @Override
    public double getListingFee(double productTotalPrice) {
        return 0;
    }

    @Override
    public double getSuccessfulTransactionFee(double orderPrice) {
        return (orderPrice * (SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE / 100f));
    }
}
