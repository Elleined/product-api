package com.elleined.marketplaceapi.service.atm.machine.transaction;

import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction atmTransaction) {
        return transactionRepository.save(atmTransaction);
    }

    @Override
    public List<WithdrawTransaction> getAllWithdrawalTransactions(User currentUser) {
        return currentUser.getWithdrawTransactions().stream()
                .sorted(Comparator.comparing(WithdrawTransaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public List<DepositTransaction> getAllDepositTransactions(User currentUser) {
        return currentUser.getDepositTransactions().stream()
                .sorted(Comparator.comparing(DepositTransaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public List<PeerToPeerTransaction> getAllReceiveMoneyTransactions(User currentUser) {
        return currentUser.getReceiveMoneyTransactions().stream()
                .sorted(Comparator.comparing(PeerToPeerTransaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public List<PeerToPeerTransaction> getAllSentMoneyTransactions(User currentUser) {
        return currentUser.getSentMoneyTransactions().stream()
                .sorted(Comparator.comparing(PeerToPeerTransaction::getTransactionDate).reversed())
                .toList();
    }
}
