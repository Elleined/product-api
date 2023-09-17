package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;

public interface EmailService {
    void sendAcceptedVerificationEmail(User userToBeVerified);

    void sendRejectedVerificationEmail(User rejectedUser, String reason);

    void sendWelcomeEmail(User registrant);

    OTPMessage sendOTP(User user);

    void sendProductListedEmail(User seller, Product product);

    void sendRejectedProductEmail(Product product, String reason);

    void sendReleaseWithdrawMail(WithdrawTransaction withdrawTransaction);
    void sendRejectedWithdrawMail(WithdrawTransaction withdrawTransaction);

    void sendReleaseDepositMail(DepositTransaction depositTransaction);
    void sendRejectDepositMail(DepositTransaction depositTransaction);
}
