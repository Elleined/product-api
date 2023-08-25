package com.elleined.marketplaceapi.service.fee;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;

public interface FeeService {

    int PREMIUM_USER_FEE = 500;
    int REFERRAL_FEE = 50;

    void deductListingFee(User seller, double listingFee);

    void payForPremium(User user);

    void payInvitingUser(User invitingUser);
}
