package com.elleined.marketplaceapi.service.fee;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elleined.marketplaceapi.model.AppWallet;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class FeeServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AppWalletService appWalletService;
    @InjectMocks
    private FeeServiceImpl feeService;

    @Test
    void deductListingFee() {
        // Expected values
        double listingFee = 10;

        BigDecimal expectedWalletBalance = new BigDecimal(listingFee);
        BigDecimal expectedUserBalance = new BigDecimal(490);
        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        AppWallet wallet = AppWallet.builder()
                .appWalletBalance(new BigDecimal(0))
                .build();

        // Stubbing methods
        when(userRepository.save(any(User.class))).thenReturn(user);
        doAnswer(i -> {
            wallet.setAppWalletBalance(wallet.getAppWalletBalance().add(new BigDecimal(listingFee)));
            return wallet;
        }).when(appWalletService).addAndSaveBalance(any());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> feeService.deductListingFee(user, listingFee));
        assertEquals(expectedWalletBalance, wallet.getAppWalletBalance());
        assertEquals(expectedUserBalance, user.getBalance());

        // Behavior verification
        verify(userRepository).save(any(User.class));
        verify(appWalletService).addAndSaveBalance(any());
    }

    @Test
    void payForPremium() {
        // Expected values
        BigDecimal expectedWalletBalance = new BigDecimal(500);
        BigDecimal expectedUserBalance = new BigDecimal(0);

        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        AppWallet wallet = AppWallet.builder()
                .appWalletBalance(new BigDecimal(0))
                .build();

        // Stubbing methods
        when(userRepository.save(any(User.class))).thenReturn(user);
        doAnswer(i -> {
            wallet.setAppWalletBalance(wallet.getAppWalletBalance().add(new BigDecimal(FeeService.PREMIUM_USER_FEE)));
            return wallet;
        }).when(appWalletService).addAndSaveBalance(any());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> feeService.payForPremium(user));
        assertEquals(expectedUserBalance, user.getBalance());
        assertEquals(expectedWalletBalance, wallet.getAppWalletBalance());

        // Behavior verification
        verify(userRepository).save(any(User.class));
        verify(appWalletService).addAndSaveBalance(any());
    }

    @Test
    void payInvitingUserForHisReferral() {
        // Expected values
        BigDecimal expectedUserBalance = new BigDecimal(550);

        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        // Stubbing methods
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> feeService.payInvitingUserForHisReferral(user));
        assertEquals(expectedUserBalance, user.getBalance(), "Failing because the expected user balance does not exact with referral fee of " + FeeService.REFERRAL_FEE);

        // Behavior verification
        verify(userRepository).save(any(User.class));
    }

    @Test
    void payExtraReferralRewardForInvitingUser() {
        // Expected values
        BigDecimal expectedUserBalance = new BigDecimal(525);

        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        // Stubbing methods
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> feeService.payExtraReferralRewardForInvitingUser(user));
        assertEquals(expectedUserBalance, user.getBalance(), "Failing because the expected user balance does not exact with extra referral fee of " + FeeService.EXTRA_REFERRAL_FEE);

        // Behavior verification
        verify(userRepository).save(any(User.class));
    }

    @Test
    void isInvitingUserLegibleForExtraReferralReward() {
        // Expected values

        // Mock Data
        Set<User> invitedUser = new HashSet<>(Arrays.asList(
                new User(),
                new User(),
                new User(),
                new User(),
                new User(),
                new User(),
                new User(),
                new User(),
                new User(),
                new User()
        ));
        User invitingUser = User.builder()
                .referredUsers(invitedUser)
                .build();

        // Stubbing methods

        // Calling the method
        // Assestions
        assertTrue(feeService.isInvitingUserLegibleForExtraReferralReward(invitingUser));

        // Behavior verification
    }

    @Test
    void deductSuccessfulTransactionFee() {
        // Expected values
        double successfulTransactionFee = 10;
        BigDecimal expectedWalletBalance = new BigDecimal(successfulTransactionFee);
        BigDecimal expectedUserBalance = new BigDecimal(490);

        // Mock Data
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        AppWallet wallet = AppWallet.builder()
                .appWalletBalance(new BigDecimal(0))
                .build();

        // Stubbing methods
        when(userRepository.save(any(User.class))).thenReturn(user);
        doAnswer(i -> {
            wallet.setAppWalletBalance(wallet.getAppWalletBalance().add(new BigDecimal(successfulTransactionFee)));
            return wallet;
        }).when(appWalletService).addAndSaveBalance(any());

        // Calling the method
        // Assestions
        assertDoesNotThrow(() -> feeService.deductSuccessfulTransactionFee(user, successfulTransactionFee));
        assertEquals(expectedUserBalance, user.getBalance());
        assertEquals(expectedWalletBalance, wallet.getAppWalletBalance());

        // Behavior verification
        verify(userRepository).save(any(User.class));
        verify(appWalletService).addAndSaveBalance(any());
    }
}