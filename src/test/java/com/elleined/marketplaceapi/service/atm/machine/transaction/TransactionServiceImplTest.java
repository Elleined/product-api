package com.elleined.marketplaceapi.service.atm.machine.transaction;

import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import com.elleined.marketplaceapi.repository.atm.TransactionRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {


    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WithdrawTransactionRepository withdrawTransactionRepository;

    @Mock
    private DepositTransactionRepository depositTransactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void save() {
        User user = new User();
        Transaction depositTransaction = DepositTransaction.builder()
                .id(1)
                .trn(UUID.randomUUID().toString())
                .amount(new BigDecimal(500))
                .status(Transaction.Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .proofOfTransaction("proofOfTransaction.jpg")
                .user(user)
                .build();

        verify(transactionRepository).save(depositTransaction);
        transactionService.save(depositTransaction);

        assertEquals(Transaction.Status.PENDING, depositTransaction.getStatus());
        assertNotNull(depositTransaction.getTrn());
        assertNotNull(depositTransaction.getAmount());
        assertNotNull(depositTransaction.getStatus());
        assertNotNull(depositTransaction.getTransactionDate());
        assertNotNull(depositTransaction.getProofOfTransaction());
    }

    @Test
    void getById() {
    }

    @Test
    void getWithdrawTransactionById() {
    }

    @Test
    void getDepositTransactionById() {
    }

    @Test
    void getAllDepositTransactionById() {
    }

    @Test
    void getAllWithdrawTransactions() {
    }

    @Test
    void getAllWithdrawalTransactions() {
    }

    @Test
    void getAllDepositTransactions() {
    }

    @Test
    void getAllReceiveMoneyTransactions() {
    }

    @Test
    void getAllSentMoneyTransactions() {
    }
}