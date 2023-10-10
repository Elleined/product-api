package com.elleined.marketplaceapi.service.atm.fee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ATMFeeServiceImpl implements ATMFeeService {

    @Override
    public float getWithdrawalFee(BigDecimal withdrawalAmount) {
        return (withdrawalAmount.intValue() * (WITHDRAWAL_FEE_PERCENTAGE / 100f));
    }

    @Override
    public float getDepositFee(BigDecimal depositedAmount) {
        return (depositedAmount.intValue() * (DEPOSIT_FEE_PERCENTAGE / 100f));
    }

    @Override
    public float getP2pFee(BigDecimal sentAmount) {
        return (sentAmount.intValue() * (P2P_FEE_PERCENTAGE / 100f));
    }
}
