package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.user.ReferralService;
import com.elleined.marketplaceapi.service.user.RegistrationPromoService;
import com.elleined.marketplaceapi.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.elleined.marketplaceapi.model.user.UserVerification.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserVerificationRequestTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RegistrationPromoService registrationPromoService;
    @Mock
    private ReferralService referralService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private ModeratorRepository moderatorRepository;
    @Mock
    private FeeService feeService;

    @InjectMocks
    private UserVerificationRequest userVerificationRequest;

    @Test
    void getAllRequest() {
        // Mock data
        User regularUser = spy(User.class);

        User premiumUser = spy(User.class);

        Premium premium = spy(Premium.class);
        premium.setUser(premiumUser);

        // Stubbing methods
        doReturn(true).when(regularUser).isNotVerified();
        doReturn(true).when(premiumUser).isNotVerified();

        doReturn(true).when(regularUser).hasShopRegistration();
        doReturn(true).when(premiumUser).hasShopRegistration();

        doReturn(true).when(regularUser).isNotRejected();
        doReturn(true).when(premiumUser).isNotRejected();

         when(premiumRepository.findAll()).thenReturn(List.of(premium));
         when(userRepository.findAll()).thenReturn(List.of(regularUser));

         List<User> actual = userVerificationRequest.getAllRequest();
         List<User> expected = Arrays.asList(premiumUser, regularUser);

        // Calling the method
        // Assertions
        assertIterableEquals(expected, actual);
        assertDoesNotThrow(() -> userVerificationRequest.getAllRequest());

        // Behavior verification
        verify(premiumRepository, atMost(2)).findAll();
        verify(userRepository, atMost(2)).findAll();
    }

    @Test
    @DisplayName("get all request validations 1: verified users must not be included")
    void whenGettingAllUnverifiedUsers_VerifiedUserMustNotBeIncluded_AndUnverifiedPremiumUsersMustBePrioritized() {
        // Mock data
        User notVerifiedRegularUser = spy(User.class);
        notVerifiedRegularUser.setUserVerification(UserVerification.builder()
                        .status(NOT_VERIFIED)
                .build());

        User notVerifiedPremiumUser = spy(User.class);
        notVerifiedPremiumUser.setUserVerification(UserVerification.builder()
                .status(NOT_VERIFIED)
                .build());
        Premium premium = Premium.builder()
                .user(notVerifiedPremiumUser)
                .build();

        User verifiedUser = spy(User.class);
        verifiedUser.setUserVerification(UserVerification.builder()
                .status(VERIFIED)
                .build());

        // Stubbing methods
        doReturn(true).when(notVerifiedRegularUser).hasShopRegistration();
        doReturn(true).when(notVerifiedPremiumUser).hasShopRegistration();

        doReturn(true).when(notVerifiedRegularUser).isNotRejected();
        doReturn(true).when(notVerifiedPremiumUser).isNotRejected();

        when(premiumRepository.findAll()).thenReturn(List.of(premium));
        when(userRepository.findAll()).thenReturn(List.of(notVerifiedRegularUser, verifiedUser));

        List<User> actual = userVerificationRequest.getAllRequest();
        List<User> expected = List.of(notVerifiedPremiumUser, notVerifiedRegularUser);

        // Calling the method
        // Assertions
        assertIterableEquals(expected, actual);
        assertDoesNotThrow(() -> userVerificationRequest.getAllRequest());

        // Behavior verification
        verify(premiumRepository, atMost(2)).findAll();
        verify(userRepository, atMost(2)).findAll();
    }

    @Test
    @DisplayName("get all request validations 2: User with no shop registration must not be included")
    void whenGettingAllUnverifiedUsers_UserWithNoShopRegistrationMustNotBeIncluded_AndUnverifiedPremiumUsersMustBePrioritized() {
        // Mock data
        User hasShopRegularUser = spy(User.class);
        hasShopRegularUser.setShop(new Shop());

        User hasShopRemiumUser = spy(User.class);
        hasShopRemiumUser.setShop(new Shop());

        Premium premium = Premium.builder()
                .user(hasShopRemiumUser)
                .build();

        User noShopUser = spy(User.class);

        // Stubbing methods
        doReturn(true).when(hasShopRegularUser).isNotVerified();
        doReturn(true).when(hasShopRemiumUser).isNotVerified();
        doReturn(true).when(noShopUser).isNotVerified();

        doReturn(true).when(hasShopRegularUser).isNotRejected();
        doReturn(true).when(hasShopRemiumUser).isNotRejected();

        when(premiumRepository.findAll()).thenReturn(List.of(premium));
        when(userRepository.findAll()).thenReturn(List.of(hasShopRegularUser, noShopUser));

        List<User> actual = userVerificationRequest.getAllRequest();
        List<User> expected = Arrays.asList(hasShopRemiumUser, hasShopRegularUser);

        // Calling the method
        // Assertions
        assertIterableEquals(expected, actual);
        assertDoesNotThrow(() -> userVerificationRequest.getAllRequest());

        // Behavior verification
        verify(premiumRepository, atMost(2)).findAll();
        verify(userRepository, atMost(2)).findAll();
    }

    @Test
    @DisplayName("get all request validations 3: rejected users must not be included")
    void whenGettingAllUnverifiedUsers_RejectedUsersMustNotBeIncluded_AndUnverifiedPremiumUsersMustBePrioritized() {
        // Mock data
        User notRejectedRegularSeller = spy(User.class);
        notRejectedRegularSeller.setUserVerification(UserVerification.builder()
                .validId("Valid id.jpg")
                .build());

        User notRejectedPremiumSeller = spy(User.class);
        notRejectedPremiumSeller.setUserVerification(UserVerification.builder()
                .validId("Valid id.jpg")
                .build());

        Premium premium = Premium.builder()
                .user(notRejectedPremiumSeller)
                .build();

        User rejectedUser = spy(User.class);
        rejectedUser.setUserVerification(UserVerification.builder()
                .validId(null)
                .build());

        // Stubbing methods
        doReturn(true).when(notRejectedRegularSeller).isNotVerified();
        doReturn(true).when(notRejectedPremiumSeller).isNotVerified();
        doReturn(true).when(rejectedUser).isNotVerified();

        doReturn(true).when(notRejectedRegularSeller).hasShopRegistration();
        doReturn(true).when(notRejectedPremiumSeller).hasShopRegistration();
        doReturn(true).when(rejectedUser).hasShopRegistration();

        when(premiumRepository.findAll()).thenReturn(List.of(premium));
        when(userRepository.findAll()).thenReturn(List.of(notRejectedRegularSeller, rejectedUser));

        List<User> actual = userVerificationRequest.getAllRequest();
        List<User> expected = Arrays.asList(notRejectedPremiumSeller, notRejectedRegularSeller);

        // Calling the method
        // Assertions
        assertIterableEquals(expected, actual);
        assertDoesNotThrow(() -> userVerificationRequest.getAllRequest());

        // Behavior verification
        verify(premiumRepository, atMost(2)).findAll();
        verify(userRepository, atMost(2)).findAll();
    }

    @Test
    void accept() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .verifiedUsers(new HashSet<>())
                .build();

        User user = User.builder()
                .userVerification(UserVerification.builder()
                        .status(NOT_VERIFIED)
                        .build())
                .build();

        // Stubbing methods
        when(registrationPromoService.isLegibleForRegistrationPromo()).thenReturn(true);
        doNothing().when(registrationPromoService).availRegistrationPromo(any(User.class));
        when(referralService.getInvitingUser(any(User.class))).thenReturn(new User());
        doNothing().when(feeService).payInvitingUserForHisReferral(any(User.class));
        when(feeService.isInvitingUserLegibleForExtraReferralReward(any(User.class))).thenReturn(true);
        doNothing().when(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> userVerificationRequest.accept(moderator, user));
        assertEquals(VERIFIED, user.getUserVerification().getStatus());
        assertTrue(moderator.getVerifiedUsers().contains(user));

        // Behavior verification
        verify(registrationPromoService).isLegibleForRegistrationPromo();
        verify(registrationPromoService).availRegistrationPromo(any(User.class));
        verify(referralService).getInvitingUser(any(User.class));
        verify(feeService).payInvitingUserForHisReferral(any(User.class));
        verify(feeService).isInvitingUserLegibleForExtraReferralReward(any(User.class));
        verify(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void first200UsersMustHaveExtra50InTheirWallet() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .verifiedUsers(new HashSet<>())
                .build();

        User user = User.builder()
                .balance(new BigDecimal(0))
                .userVerification(UserVerification.builder()
                        .status(NOT_VERIFIED)
                        .build())
                .build();

        // Stubbing methods
        when(registrationPromoService.isLegibleForRegistrationPromo()).thenReturn(true);
        doAnswer(i -> {
            user.setBalance(user.getBalance().add(RegistrationPromoService.REGISTRATION_REWARD));
            return user;
        }).when(registrationPromoService).availRegistrationPromo(any(User.class));

        when(referralService.getInvitingUser(any(User.class))).thenReturn(new User());
        doNothing().when(feeService).payInvitingUserForHisReferral(any(User.class));
        when(feeService.isInvitingUserLegibleForExtraReferralReward(any(User.class))).thenReturn(true);
        doNothing().when(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        BigDecimal expectedBalance = new BigDecimal(50);
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> userVerificationRequest.accept(moderator, user));
        assertEquals(expectedBalance, user.getBalance());
        assertEquals(VERIFIED, user.getUserVerification().getStatus());
        assertTrue(moderator.getVerifiedUsers().contains(user));

        // Behavior verification
        verify(registrationPromoService).isLegibleForRegistrationPromo();
        verify(registrationPromoService).availRegistrationPromo(any(User.class));
        verify(referralService).getInvitingUser(any(User.class));
        verify(feeService).payInvitingUserForHisReferral(any(User.class));
        verify(feeService).isInvitingUserLegibleForExtraReferralReward(any(User.class));
        verify(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void invitingUserMustBePaid50IfHisInvitedUserGetsVerified() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .verifiedUsers(new HashSet<>())
                .build();

        User invitedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(NOT_VERIFIED)
                        .build())
                .build();

        User invitingUser = User.builder()
                .balance(new BigDecimal(0))
                .build();

        // Stubbing methods
        when(registrationPromoService.isLegibleForRegistrationPromo()).thenReturn(true);
        doNothing().when(registrationPromoService).availRegistrationPromo(any(User.class));

        when(referralService.getInvitingUser(any(User.class))).thenReturn(invitingUser);
        doAnswer(i -> {
            invitingUser.setBalance(invitingUser.getBalance().add(new BigDecimal(FeeService.REFERRAL_FEE)));
            return invitingUser;
        }).when(feeService).payInvitingUserForHisReferral(any(User.class));

        when(feeService.isInvitingUserLegibleForExtraReferralReward(any(User.class))).thenReturn(true);
        doNothing().when(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Expected/ Actual values
        BigDecimal expectedInvitingUserBalance = new BigDecimal(50);

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> userVerificationRequest.accept(moderator, invitedUser));
        assertEquals(expectedInvitingUserBalance, invitingUser.getBalance());

        assertEquals(VERIFIED, invitedUser.getUserVerification().getStatus());
        assertTrue(moderator.getVerifiedUsers().contains(invitedUser));

        // Behavior verification
        verify(registrationPromoService).isLegibleForRegistrationPromo();
        verify(registrationPromoService).availRegistrationPromo(any(User.class));
        verify(referralService).getInvitingUser(any(User.class));
        verify(feeService).payInvitingUserForHisReferral(any(User.class));
        verify(feeService).isInvitingUserLegibleForExtraReferralReward(any(User.class));
        verify(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void invitingUserMustBePaid50WithExtra25ForEvery10thUserHeGetInvited() {
        // Mock data
        Moderator moderator = Moderator.builder()
                .verifiedUsers(new HashSet<>())
                .build();

        User invitedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(NOT_VERIFIED)
                        .build())
                .build();

        User invitingUser = User.builder()
                .balance(new BigDecimal(0))
                .build();

        // Stubbing methods
        when(registrationPromoService.isLegibleForRegistrationPromo()).thenReturn(true);
        doNothing().when(registrationPromoService).availRegistrationPromo(any(User.class));

        when(referralService.getInvitingUser(any(User.class))).thenReturn(invitingUser);
        doAnswer(i -> {
            invitingUser.setBalance(invitingUser.getBalance().add(new BigDecimal(FeeService.REFERRAL_FEE)));
            return invitingUser;
        }).when(feeService).payInvitingUserForHisReferral(any(User.class));

        when(feeService.isInvitingUserLegibleForExtraReferralReward(any(User.class))).thenReturn(true);
        doAnswer(i -> {
            invitingUser.setBalance(invitingUser.getBalance().add(new BigDecimal(FeeService.EXTRA_REFERRAL_FEE)));
            return invitingUser;
        }).when(feeService).payExtraReferralRewardForInvitingUser(any(User.class));

        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(new Moderator());

        // Expected/ Actual values
        BigDecimal expectedInvitingUserBalance = new BigDecimal(75);

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> userVerificationRequest.accept(moderator, invitedUser));
        assertEquals(expectedInvitingUserBalance, invitingUser.getBalance());

        assertEquals(VERIFIED, invitedUser.getUserVerification().getStatus());
        assertTrue(moderator.getVerifiedUsers().contains(invitedUser));

        // Behavior verification
        verify(registrationPromoService).isLegibleForRegistrationPromo();
        verify(registrationPromoService).availRegistrationPromo(any(User.class));
        verify(referralService).getInvitingUser(any(User.class));
        verify(feeService).payInvitingUserForHisReferral(any(User.class));
        verify(feeService).isInvitingUserLegibleForExtraReferralReward(any(User.class));
        verify(feeService).payExtraReferralRewardForInvitingUser(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void reject() {
        // Mock data
        User user = User.builder()
                .userVerification(UserVerification.builder()
                        .status(NOT_VERIFIED)
                        .build())
                .build();

        // Stubbing methods
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Calling the method
        userVerificationRequest.reject(any(Moderator.class), user);

        // Assertions
        assertEquals(NOT_VERIFIED, user.getUserVerification().getStatus());
        assertNull(user.getUserVerification().getValidId());

        // Behavior verification
        verify(userRepository).save(any(User.class));
    }
}