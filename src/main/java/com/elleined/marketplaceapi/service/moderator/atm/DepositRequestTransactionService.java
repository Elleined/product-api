package com.elleined.marketplaceapi.service.moderator.atm;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.repository.atm.transaction.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("depositRequestTransactionService")
public class DepositRequestTransactionService implements RequestTransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction getAllPendingRequest() {
        return null;
    }

    @Override
    public Transaction release(Moderator moderator, Transaction transaction) {
        return null;
    }

    @Override
    public void reject(Moderator moderator, Transaction transaction) {

    }

    @Override
    public List<Transaction> releaseAll(Moderator moderator, List<Transaction> transactions) {
        return null;
    }

    @Override
    public void rejectAll(Moderator moderator, List<Transaction> requestTransactions) {

    }
}
