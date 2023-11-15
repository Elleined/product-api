package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserVerificationRequest implements Request<User> {
    private final UserRepository userRepository;
    private final UserService userService;
    private final RegistrationPromoService registrationPromoService;
    private final ReferralService referralService;
    private final PremiumRepository premiumRepository;

    private final ModeratorRepository moderatorRepository;

    private final FeeService feeService;

    @Override
    public List<User> getAllRequest() {
        List<User> premiumUsers = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(User::hasShopRegistration)
                .filter(User::isNotRejected) // Checking for rejected user
                .toList();

        List<User> regularUsers = userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(User::hasShopRegistration)
                .filter(User::isNotRejected) // Checking for rejected user
                .toList();

        List<User> users = new ArrayList<>();
        users.addAll(premiumUsers);
        users.addAll(regularUsers);
        return users;
    }

    @Override
    public void accept(Moderator moderator, User userToBeVerified) {
        if (registrationPromoService.isLegibleForRegistrationPromo()) registrationPromoService.availRegistrationPromo(userToBeVerified);
        User invitingUser = referralService.getInvitingUser(userToBeVerified);
        if (invitingUser != null) feeService.payInvitingUserForHisReferral(invitingUser);
        if (invitingUser != null && feeService.isInvitingUserLegibleForExtraReferralReward(invitingUser)) feeService.payExtraReferralRewardForInvitingUser(invitingUser);

        userToBeVerified.getUserVerification().setStatus(UserVerification.Status.VERIFIED);
        moderator.addVerifiedUser(userToBeVerified);

        userRepository.save(userToBeVerified);
        moderatorRepository.save(moderator);

        log.debug("User with id of {} are now verified", userToBeVerified.getId());
    }

    @Override
    public void acceptAll(Moderator moderator, Set<User> usersToBeVerified) {
        usersToBeVerified.forEach(userToBeVerified -> this.accept(moderator, userToBeVerified));
        log.debug("Users with id of {} are now verified", usersToBeVerified.stream().map(User::getId).toList());
    }

    @Override
    public void reject(Moderator moderator, User userToBeRejected) {
        userToBeRejected.getUserVerification().setStatus(UserVerification.Status.NOT_VERIFIED);
        userToBeRejected.getUserVerification().setValidId(null);
        userRepository.save(userToBeRejected);
        log.debug("User with id of {} application for verification are rejected by the moderator!", userToBeRejected.getId());
    }

    @Override
    public void rejectAll(Moderator moderator, Set<User> usersToBeRejected) {
        usersToBeRejected.forEach(userToBeRejected -> this.reject(moderator, userToBeRejected));
        log.debug("Users with id of {} rejected successfully", usersToBeRejected.stream().map(User::getId).toList());
    }
}
