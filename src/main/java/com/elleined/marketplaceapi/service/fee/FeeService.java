package com.elleined.marketplaceapi.service.fee;

import com.elleined.marketplaceapi.model.user.User;

public interface FeeService {

    void deductListingFee(User seller, double listingFee);

}
