package com.elleined.marketplaceapi.service.atm.machine.p2p;

import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.TransactionService;

import java.math.BigDecimal;
import java.util.List;

public interface P2PTransactionService extends TransactionService<PeerToPeerTransaction> {
    PeerToPeerTransaction save(User sender, User receiver, BigDecimal sentAmount);
    List<PeerToPeerTransaction> getAllReceiveMoneyTransactions(User currentUser);
    List<PeerToPeerTransaction> getAllSentMoneyTransactions(User currentUser);

}
