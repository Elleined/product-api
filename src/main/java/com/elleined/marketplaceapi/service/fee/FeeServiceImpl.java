package com.elleined.marketplaceapi.service.fee;

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
}
