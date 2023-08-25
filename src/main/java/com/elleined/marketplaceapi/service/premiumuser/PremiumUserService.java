package com.elleined.marketplaceapi.service.premiumuser;

import com.elleined.marketplaceapi.exception.InsufficientBalanceException;
import com.elleined.marketplaceapi.model.user.User;

public interface PremiumUserService {
    void upgradeToPremium(User user) throws InsufficientBalanceException;

    boolean isBalanceNotEnoughForPremium(User user);

    boolean isPremiumUser(User user);
}
