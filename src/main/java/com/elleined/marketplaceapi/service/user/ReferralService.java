package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;

public interface ReferralService {
    void addInvitedUser(String invitingUserReferralCode, User invitedUser);

    // returns the inviting user of invited user
    User getInvitingUser(User invitedUser) throws ResourceNotFoundException;
}
