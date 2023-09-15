package com.elleined.marketplaceapi.repository.atm.transaction;

import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawTransactionRepository extends TransactionRepository<WithdrawTransaction> {
}
