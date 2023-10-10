package com.elleined.marketplaceapi.controller.atm.constants;

import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.DepositService;
import com.elleined.marketplaceapi.service.atm.machine.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/withdraw")
@RequiredArgsConstructor
public class WithdrawConstantsController {

    @GetMapping("/fee")
    public int getFee() {
        return ATMFeeService.WITHDRAWAL_FEE_PERCENTAGE;
    }
    @GetMapping("/limit-per-day")
    public int getLimitPerDay() {
        return WithdrawService.WITHDRAWAL_LIMIT_PER_DAY;
    }

    @GetMapping("/maximum-amount")
    public int getMaximumAmount() {
        return WithdrawService.MAXIMUM_WITHDRAW_AMOUNT;
    }

    @GetMapping("/minimum-amount")
    public int getMinimumAmount() {
        return WithdrawService.MINIMUM_WITHDRAW_AMOUNT;
    }
}
