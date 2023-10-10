package com.elleined.marketplaceapi.controller.constants;

import com.elleined.marketplaceapi.service.user.seller.premium.PremiumSellerProxy;
import com.elleined.marketplaceapi.service.user.seller.regular.RegularSellerProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FeeController {

    @GetMapping("/premium-seller-successful-transaction-fee")
    public float getPremiumSellerSuccessfulTransactionFee() {
        return PremiumSellerProxy.SUCCESSFUL_TRANSACTION_FEE;
    }

    @GetMapping("/regular-seller-listing-fee")
    public float getRegularSellerListingFee() {
        return RegularSellerProxy.LISTING_FEE_PERCENTAGE;
    }

    @GetMapping("/regular-seller-successful-transaction-fee")
    public float getRegularSellerSuccessfulTransactionFee() {
        return RegularSellerProxy.SUCCESSFUL_TRANSACTION_FEE;
    }
}
