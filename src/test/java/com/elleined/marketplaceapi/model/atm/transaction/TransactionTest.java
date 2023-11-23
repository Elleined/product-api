package com.elleined.marketplaceapi.model.atm.transaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionTest {

    @Test
    void isRejected() {
        // Expected and Actual Value

        // Mock Data
        Transaction depositTransaction = new DepositTransaction();
        Transaction withdrawTransaction = new WithdrawTransaction();
        Transaction peerToPeerTransaction = new PeerToPeerTransaction();

        depositTransaction.setStatus(Transaction.Status.REJECTED);
        withdrawTransaction.setStatus(Transaction.Status.REJECTED);
        peerToPeerTransaction.setStatus(Transaction.Status.REJECTED);
        // Stubbing methods

        // Calling the method


        // Assertions
        assertEquals(Transaction.Status.REJECTED, withdrawTransaction.getStatus());
        assertEquals(Transaction.Status.REJECTED, depositTransaction.getStatus());
        assertEquals(Transaction.Status.REJECTED, peerToPeerTransaction.getStatus());

        // Behavior Verifications
    }

    @Test
    void isRelease() {
        // Expected and Actual Value

        // Mock Data
        Transaction depositTransaction = new DepositTransaction();
        Transaction withdrawTransaction = new WithdrawTransaction();
        Transaction peerToPeerTransaction = new PeerToPeerTransaction();

        depositTransaction.setStatus(Transaction.Status.RELEASE);
        withdrawTransaction.setStatus(Transaction.Status.RELEASE);
        peerToPeerTransaction.setStatus(Transaction.Status.RELEASE);
        // Stubbing methods

        // Calling the method


        // Assertions
        assertEquals(Transaction.Status.RELEASE, withdrawTransaction.getStatus());
        assertEquals(Transaction.Status.RELEASE, depositTransaction.getStatus());
        assertEquals(Transaction.Status.RELEASE, peerToPeerTransaction.getStatus());

        // Behavior Verifications
    }

    @Test
    void isPending() {
        // Expected and Actual Value

        // Mock Data
        Transaction depositTransaction = new DepositTransaction();
        Transaction withdrawTransaction = new WithdrawTransaction();
        Transaction peerToPeerTransaction = new PeerToPeerTransaction();

        depositTransaction.setStatus(Transaction.Status.PENDING);
        withdrawTransaction.setStatus(Transaction.Status.PENDING);
        peerToPeerTransaction.setStatus(Transaction.Status.PENDING);
        // Stubbing methods

        // Calling the method


        // Assertions
        assertEquals(Transaction.Status.PENDING, withdrawTransaction.getStatus());
        assertEquals(Transaction.Status.PENDING, depositTransaction.getStatus());
        assertEquals(Transaction.Status.PENDING, peerToPeerTransaction.getStatus());

        // Behavior Verifications
    }
}