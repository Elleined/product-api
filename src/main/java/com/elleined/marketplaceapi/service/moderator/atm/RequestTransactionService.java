package com.elleined.marketplaceapi.service.moderator.atm;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;

import java.util.List;

public interface RequestTransactionService {
    Transaction getAllPendingRequest();
    Transaction release(Moderator moderator, Transaction transaction);
    void reject(Moderator moderator, Transaction transaction);
    List<Transaction> releaseAll(Moderator moderator, List<Transaction> transactions);
    void rejectAll(Moderator moderator, List<Transaction> requestTransactions);
}
