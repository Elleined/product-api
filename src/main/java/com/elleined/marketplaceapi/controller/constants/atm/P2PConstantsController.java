package com.elleined.marketplaceapi.controller.constants.atm;

import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.PeerToPeerService;
import com.elleined.marketplaceapi.service.atm.machine.WithdrawService;
import com.elleined.marketplaceapi.service.fee.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constants/p2p")
@RequiredArgsConstructor
public class P2PConstantsController {

    @GetMapping("/fee")
    public int getFee() {
        return ATMFeeService.P2P_FEE_PERCENTAGE;
    }

    @GetMapping("/limit-per-day")
    public int getLimitPerDay() {
        return PeerToPeerService.PEER_TO_PEER_LIMIT_PER_DAY;
    }

    @GetMapping("/maximum-amount")
    public int getMaximumAmount() {
        return PeerToPeerService.MAXIMUM_AMOUNT;
    }

    @GetMapping("/minimum-amount")
    public int getMinimumAmount() {
        return PeerToPeerService.MINIMUM_AMOUNT;
    }
}
