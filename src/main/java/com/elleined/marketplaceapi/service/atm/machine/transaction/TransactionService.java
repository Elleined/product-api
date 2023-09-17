package com.elleined.marketplaceapi.service.atm.machine.transaction;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;
import java.util.Set;

public interface TransactionService {

    Transaction save(Transaction transaction);

    Transaction getById(int id) throws ResourceNotFoundException;
    WithdrawTransaction getWithdrawTransactionById(int withdrawTransactionId) throws ResourceNotFoundException;
    DepositTransaction getDepositTransactionById(int depositTransactionId) throws ResourceNotFoundException;
    List<DepositTransaction> getAllDepositTransactionById(Set<Integer> depositTransactionIds);
    List<WithdrawTransaction> getAllWithdrawTransactions(Set<Integer> withdrawTransactionIds);
    List<WithdrawTransaction> getAllWithdrawalTransactions(User currentUser);
    List<DepositTransaction> getAllDepositTransactions(User currentUser);
    List<PeerToPeerTransaction> getAllReceiveMoneyTransactions(User currentUser);
    List<PeerToPeerTransaction> getAllSentMoneyTransactions(User currentUser);

}
