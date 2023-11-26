package com.elleined.marketplaceapi.service.user.seller.fee;

public interface SellerFeeService {
    double getListingFee(double productTotalPrice);
    double getSuccessfulTransactionFee(double orderPrice);
}
