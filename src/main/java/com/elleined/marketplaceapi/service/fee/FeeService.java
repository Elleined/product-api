package com.elleined.marketplaceapi.service.fee;

import com.elleined.marketplaceapi.model.user.User;

public interface FeeService {

    int PREMIUM_USER_FEE = 500;

    int EXTRA_REFERRAL_FEE_LEGIBILITY = 10;

    int EXTRA_REFERRAL_FEE = 25;
    int REFERRAL_FEE = 50;

    void deductListingFee(User seller, double listingFee);

    void payForPremium(User user);

    void payInvitingUserForHisReferral(User invitingUser);

    void payExtraReferralRewardForInvitingUser(User invitingUser);

    boolean isUserHasLegibleForExtraReferralReward(User invitingUser);
}
