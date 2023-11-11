package com.elleined.marketplaceapi.service.atm.machine.transaction;

import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface P2PTransactionService {
    List<PeerToPeerTransaction> getAllReceiveMoneyTransactions(User currentUser);
    List<PeerToPeerTransaction> getAllSentMoneyTransactions(User currentUser);
}
