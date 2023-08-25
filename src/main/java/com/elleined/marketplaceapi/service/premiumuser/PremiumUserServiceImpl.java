package com.elleined.marketplaceapi.service.premiumuser;

import com.elleined.marketplaceapi.exception.InsufficientBalanceException;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.service.fee.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PremiumUserServiceImpl implements PremiumUserService {
    private final PremiumRepository premiumRepository;
    private final FeeService feeService;

    @Override
    public void upgradeToPremium(User user) throws InsufficientBalanceException {
        if (isPremiumUser(user)) return;
        if (isBalanceNotEnoughForPremium(user)) throw new InsufficientBalanceException("User with id of " + user.getId() + " doesn't have enough balance to pay for the premium user fee amounting " + FeeService.PREMIUM_USER_FEE);
        Premium premium = Premium.builder()
                .registrationDate(LocalDateTime.now())
                .user(user)
                .build();
        premiumRepository.save(premium);
        feeService.payForPremium(user);

        log.debug("User with id of " + user.getId() + " are now upgraded to premium");
    }

    @Override
    public boolean isBalanceNotEnoughForPremium(User user) {
        return user.getBalance().compareTo(new BigDecimal(FeeService.PREMIUM_USER_FEE)) <= 0;
    }

    @Override
    public boolean isPremiumUser(User user) {
        return user.getPremium() != null;
    }
}
