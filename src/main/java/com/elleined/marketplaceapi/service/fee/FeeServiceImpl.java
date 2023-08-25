package com.elleined.marketplaceapi.service.fee;

import com.elleined.marketplaceapi.exception.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.AppWallet;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.AppWalletRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
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
    private final AppWalletRepository appWalletRepository;
    @Override
    public void deductListingFee(User seller, double listingFee) {
        BigDecimal fee = new BigDecimal(listingFee);
        BigDecimal oldSellerBalance = seller.getBalance();
        BigDecimal sellerNewBalance = seller.getBalance().subtract(fee);
        seller.setBalance(sellerNewBalance);

        AppWallet appWallet = appWalletRepository.findById(1).orElseThrow();
        BigDecimal oldAppWalletBalance = appWallet.getAppWalletBalance();
        BigDecimal newAppWalletBalance = appWallet.getAppWalletBalance().add(fee);
        appWallet.setAppWalletBalance(newAppWalletBalance);

        userRepository.save(seller);
        appWalletRepository.save(appWallet);

        log.debug("Seller with id of {} has now new balance of {} because listing fee is deducted which is {} from old balance of {}", seller.getId(), sellerNewBalance, fee, oldSellerBalance);
        log.debug("Appwallet has now new balance of {} from old balance of {} because listing fee is added which is {}", newAppWalletBalance, oldAppWalletBalance, fee);
    }

    @Override
    public void payForPremium(User user) {
        BigDecimal userOldBalance = user.getBalance();
        user.setBalance(userOldBalance.subtract(new BigDecimal(PREMIUM_USER_FEE)));
        BigDecimal userNewBalance = user.getBalance();
        userRepository.save(user);

        AppWallet appWallet = appWalletRepository.findById(1).orElseThrow();
        BigDecimal oldAppWalletBalance = appWallet.getAppWalletBalance();
        appWallet.setAppWalletBalance(oldAppWalletBalance.add(new BigDecimal(PREMIUM_USER_FEE)));
        BigDecimal newAppWalletBalance = appWallet.getAppWalletBalance();
        appWalletRepository.save(appWallet);

        log.debug("User with id of {} buys premium account amounting {}. This amount is deducted to his/her account balance and now has new balance of {} from {}", user.getId(), PREMIUM_USER_FEE, userNewBalance, userOldBalance);
        log.debug("Premium user fee has been added to app wallet and now has new balance of {} from {}", newAppWalletBalance, oldAppWalletBalance);
    }

    @Override
    public void payInvitingUser(String invitingUserReferralCode) throws ResourceNotFoundException {
        User invitingUser = userRepository.fetchByReferralCode(invitingUserReferralCode).orElseThrow(() -> new ResourceNotFoundException("User with referral code of " + invitingUserReferralCode + " does not exists!"));
        BigDecimal oldUserBalance = invitingUser.getBalance();
        invitingUser.setBalance(oldUserBalance.add(new BigDecimal(REFERRAL_FEE)));
        userRepository.save(invitingUser);
        log.debug("Inviting user with id of {} successfully invited this user and now has new balance of {} from {}", invitingUser.getId(), invitingUser.getBalance(), oldUserBalance);
    }
}
