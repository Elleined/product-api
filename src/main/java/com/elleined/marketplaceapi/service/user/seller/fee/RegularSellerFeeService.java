package com.elleined.marketplaceapi.service.user.seller.fee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Primary
public class RegularSellerFeeService implements SellerFeeService {

    public final static float LISTING_FEE_PERCENTAGE = 2;
    public final static float SUCCESSFUL_TRANSACTION_FEE = 2;

    @Override
    public double getListingFee(double productTotalPrice) {
        return (productTotalPrice * (LISTING_FEE_PERCENTAGE / 100f));
    }

    @Override
    public double getSuccessfulTransactionFee(double orderPrice) {
        return (orderPrice * (SUCCESSFUL_TRANSACTION_FEE / 100f));
    }
}
