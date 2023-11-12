package com.elleined.marketplaceapi.controller.constants.atm;

import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.deposit.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constants/deposit")
@RequiredArgsConstructor
public class DepositConstantsController {

    @GetMapping("/fee")
    public int getFee() {
        return ATMFeeService.DEPOSIT_FEE_PERCENTAGE;
    }
    @GetMapping("/limit-per-day")
    public int getLimitPerDay() {
        return DepositService.DEPOSIT_LIMIT_PER_DAY;
    }

    @GetMapping("/minimum-amount")
    public int getMinimumAmount() {
        return DepositService.MINIMUM_DEPOSIT_AMOUNT;
    }
}
