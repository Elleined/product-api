package com.elleined.marketplaceapi.repository.atm.transaction;

import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositTransactionRepository extends TransactionRepository<DepositTransaction> {
}
