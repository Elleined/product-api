package com.elleined.marketplaceapi.service.atm.machine.withdraw;

import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.TransactionService;

import java.math.BigDecimal;

public interface WithdrawTransactionService extends TransactionService<WithdrawTransaction> {
    WithdrawTransaction save(User user, BigDecimal withdrawalAmount, String gcashNumber);
}
