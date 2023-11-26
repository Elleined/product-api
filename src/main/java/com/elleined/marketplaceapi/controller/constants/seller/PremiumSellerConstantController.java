package com.elleined.marketplaceapi.controller.constants.seller;

import com.elleined.marketplaceapi.service.user.seller.fee.PremiumSellerFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constants/premium-seller")
@RequiredArgsConstructor
public class PremiumSellerConstantController {

    @GetMapping("/transaction-fee")
    public float getTransactionFee() {
        return PremiumSellerFeeService.SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE;
    }
}
