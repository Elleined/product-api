package com.elleined.marketplaceapi.controller.constants;

import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.user.PremiumService;
import com.elleined.marketplaceapi.service.user.RegistrationPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constants")
@RequiredArgsConstructor
public class ConstantsController {

    @GetMapping("/premium-fee")
    public int getPremiumFee() {
        return FeeService.PREMIUM_USER_FEE;
    }

    @GetMapping("/referral-reward")
    public int getReferralFee() {
        return FeeService.REFERRAL_FEE;
    }

    @GetMapping("/extra-referral-reward")
    public int getExtraReferralReward() {
        return FeeService.EXTRA_REFERRAL_FEE;
    }

    @GetMapping("/registration-limit")
    public int getRegistrationLimit() {
        return RegistrationPromoService.REGISTRATION_LIMIT_PROMO;
    }
}
