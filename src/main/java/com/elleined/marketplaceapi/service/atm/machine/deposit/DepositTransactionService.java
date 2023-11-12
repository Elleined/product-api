package com.elleined.marketplaceapi.service.atm.machine.deposit;

import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.TransactionService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface DepositTransactionService extends TransactionService<DepositTransaction> {
    DepositTransaction save(User user, BigDecimal depositedAmount, MultipartFile proofOfTransaction);
}
