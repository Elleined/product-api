package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.service.fee.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final FeeService feeService;


    public void upgradeToPremium(User user)
            throws InsufficientBalanceException, AlreadyExistException {

        if (user.isPremiumAndNotExpired())
            throw new AlreadyExistException("Cannot buy premium! because you already purchased our premium account please wait for your premium account to expired which is " + user.getPremium().getRegistrationDate().plusMonths(1) + " before purchasing again");
        if (user.isBalanceNotEnough(FeeService.PREMIUM_USER_FEE))
            throw new InsufficientBalanceException("Cannot buy premium because! you doesn't have enough balance to pay for the premium user fee amounting " + FeeService.PREMIUM_USER_FEE);
        Premium premium = Premium.builder()
                .registrationDate(LocalDateTime.now())
                .user(user)
                .build();
        premiumRepository.save(premium);
        feeService.payForPremium(user);

        log.debug("User with id of " + user.getId() + " are now upgraded to premium");
    }
}
