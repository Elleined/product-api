package com.elleined.marketplaceapi.service.fee;

import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FeeServiceImpl implements FeeService {
    private final UserRepository userRepository;
    private final AppWalletService appWalletService;
    @Override
    public void deductListingFee(User seller, double listingFee) {
        BigDecimal fee = new BigDecimal(listingFee);
        BigDecimal oldSellerBalance = seller.getBalance();
        BigDecimal sellerNewBalance = seller.getBalance().subtract(fee);
        seller.setBalance(sellerNewBalance);

        userRepository.save(seller);
        appWalletService.addAndSaveBalance(fee);

        log.debug("Seller with id of {} has now new balance of {} because listing fee is deducted which is {} from old balance of {}", seller.getId(), sellerNewBalance, fee, oldSellerBalance);
    }

    @Override
    public void payForPremium(User user) {
        BigDecimal userOldBalance = user.getBalance();
        user.setBalance(userOldBalance.subtract(new BigDecimal(PREMIUM_USER_FEE)));
        BigDecimal userNewBalance = user.getBalance();
        userRepository.save(user);
        appWalletService.addAndSaveBalance(PREMIUM_USER_FEE);

        log.debug("User with id of {} buys premium account amounting {}. This amount is deducted to his/her account balance and now has new balance of {} from {}", user.getId(), PREMIUM_USER_FEE, userNewBalance, userOldBalance);
    }

    @Override
    public void payInvitingUserForHisReferral(User invitingUser) {
        BigDecimal oldUserBalance = invitingUser.getBalance();
        invitingUser.setBalance(oldUserBalance.add(new BigDecimal(REFERRAL_FEE)));
        userRepository.save(invitingUser);
        log.debug("Inviting user with id of {} successfully invited this user and now has new balance of {} from {}", invitingUser.getId(), invitingUser.getBalance(), oldUserBalance);
    }

    @Override
    public void payExtraReferralRewardForInvitingUser(User invitingUser) {
        BigDecimal oldBalance = invitingUser.getBalance();
        invitingUser.setBalance(oldBalance.add(new BigDecimal(EXTRA_REFERRAL_FEE)));
        userRepository.save(invitingUser);
        log.debug("User with id of {} has reached his {}th invited user and now has legible for extra referral fee of {} and now has new balance of {} from {}", invitingUser.getId(), EXTRA_REFERRAL_FEE_LEGIBILITY, EXTRA_REFERRAL_FEE, invitingUser.getBalance(), oldBalance);
    }

    @Override
    public boolean isInvitingUserLegibleForExtraReferralReward(User invitingUser) {
        return invitingUser.getReferredUsers().size() % EXTRA_REFERRAL_FEE_LEGIBILITY == 0;
    }

    @Override
    public void deductSuccessfulTransactionFee(User seller, double successfulTransactionFee) {
        BigDecimal userOldBalance = seller.getBalance();
        seller.setBalance(userOldBalance.subtract(new BigDecimal(successfulTransactionFee)));
        userRepository.save(seller);
        appWalletService.addAndSaveBalance(successfulTransactionFee);
        log.debug("Successful transaction fee of {} has been deducted to user with id of {} and now has new balance of {} from {}", successfulTransactionFee, seller.getId(), seller.getBalance(), userOldBalance);
    }
}
