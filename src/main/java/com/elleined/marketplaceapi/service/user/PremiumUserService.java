package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface PremiumUserService extends SellerService {
    void buyPremium(User user);

    List<User> getAllPremiumUser();
}
