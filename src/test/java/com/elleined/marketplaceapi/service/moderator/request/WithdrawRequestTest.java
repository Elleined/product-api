package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import com.elleined.marketplaceapi.service.atm.machine.withdraw.WithdrawService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawRequestTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private ModeratorRepository moderatorRepository;

    @Mock
    private WithdrawService withdrawService;
    @Mock
    private WithdrawTransactionRepository withdrawTransactionRepository;

    @InjectMocks
    private WithdrawRequest withdrawRequest;

    @Test
    void getAllRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void accept() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void reject() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }
}