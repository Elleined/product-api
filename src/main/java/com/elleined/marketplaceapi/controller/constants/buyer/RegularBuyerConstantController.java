package com.elleined.marketplaceapi.controller.constants.buyer;

import com.elleined.marketplaceapi.service.user.buyer.regular.RegularBuyerRestriction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constants/regular-buyer")
@RequiredArgsConstructor
public class RegularBuyerConstantController {

    @GetMapping("/max-order-per-day")
    public int getMaxOrderPerDay() {
        return RegularBuyerRestriction.MAX_ORDER_PER_DAY;
    }
}
