package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.PeerToPeerTransactionRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.p2p.PeerToPeerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PeerToPeerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ATMFeeService feeService;

    @Mock
    private PeerToPeerTransactionRepository peerToPeerTransactionRepository;

    @Mock
    private AppWalletService appWalletService;

    @InjectMocks
    private PeerToPeerService peerToPeerService;



    @Test
    void peerToPeer() {
    }

    @Test
    void shouldThrowSendingToHimSelfException() {
        User sender = User.builder()
                .id(1)
                .build();

        User receiver = User.builder()
                .id(1)
                .build();



        verifyNoInteractions(userRepository, appWalletService, peerToPeerTransactionRepository);
    }

    @Test
    void reachedLimitAmountPerDay() {
    }

    @Test
    void getAllReceiveMoneyTransactions() {
    }

    @Test
    void getAllSentMoneyTransactions() {
    }

    @Test
    void save() {
    }

    @Test
    void getById() {
    }

    @Test
    void getAllById() {
    }

    @Test
    void getAll() {
    }
}