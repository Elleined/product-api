package com.elleined.marketplaceapi.service.atm.fee;

import java.math.BigDecimal;

public interface ATMFeeService {
    int DEPOSIT_FEE_PERCENTAGE = 2;
    int WITHDRAWAL_FEE_PERCENTAGE = 2;
    int P2P_FEE_PERCENTAGE = 2;

    float getWithdrawalFee(BigDecimal withdrawalAmount);
    float getDepositFee(BigDecimal depositedAmount);
    float getP2pFee(BigDecimal sentAmount);

    BigDecimal deductDepositFee(BigDecimal depositedAmount, float depositFee);

    BigDecimal deductWithdrawalFee(BigDecimal withdrawnAmount, float withdrawalFee);

    BigDecimal deductP2pFee(BigDecimal sentAmount, float p2pFee);
}
