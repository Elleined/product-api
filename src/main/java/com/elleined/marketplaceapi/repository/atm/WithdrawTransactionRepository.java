package com.elleined.marketplaceapi.repository.atm;

import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawTransactionRepository extends JpaRepository<WithdrawTransaction, Integer> {
}