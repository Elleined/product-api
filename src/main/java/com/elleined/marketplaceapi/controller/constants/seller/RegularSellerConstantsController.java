package com.elleined.marketplaceapi.controller.constants.seller;


import com.elleined.marketplaceapi.service.user.seller.fee.RegularSellerFeeService;
import com.elleined.marketplaceapi.service.user.seller.regular.RegularSellerRestriction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constants/regular-seller")
@RequiredArgsConstructor
public class RegularSellerConstantsController {
    @GetMapping("/listing-fee")
    public float getListingFee() {
        return RegularSellerFeeService.LISTING_FEE_PERCENTAGE;
    }

    @GetMapping("/transaction-fee")
    public float getTransactionFee() {
        return RegularSellerFeeService.SUCCESSFUL_TRANSACTION_FEE;
    }

    @GetMapping("/max-accepted-order")
    public int getMaxAcceptedOrder() {
        return RegularSellerRestriction.MAX_ACCEPTED_ORDER;
    }

    @GetMapping("/max-pending-order")
    public int getMaxPendingOrder() {
        return RegularSellerRestriction.MAX_PENDING_ORDER;
    }

    @GetMapping("/max-rejection-per-day")
    public int getRejectionPerDay() {
        return RegularSellerRestriction.MAX_ORDER_REJECTION_PER_DAY;
    }

    @GetMapping("/max-listing-per-day")
    public int getMaxListingPerDay() {
        return RegularSellerRestriction.MAX_LISTING_PER_DAY;
    }
}
