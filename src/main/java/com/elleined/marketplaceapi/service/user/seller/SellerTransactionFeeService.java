package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.model.user.User;

public interface SellerTransactionFeeService {

    boolean isBalanceNotEnoughToPaySuccessfulTransactionFee(User seller, double successfulTransactionFee);

    double getSuccessfulTransactionFee(double orderItemPrice);
}
