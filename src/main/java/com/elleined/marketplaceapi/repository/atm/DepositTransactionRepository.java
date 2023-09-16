package com.elleined.marketplaceapi.repository.atm;

import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositTransactionRepository extends JpaRepository<DepositTransaction, Integer> {
}