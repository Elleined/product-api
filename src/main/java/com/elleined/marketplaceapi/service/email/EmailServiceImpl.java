package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.client.EmailClient;
import com.elleined.marketplaceapi.dto.email.EmailMessage;
import com.elleined.marketplaceapi.dto.email.OTPMessage;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailClient emailClient;
    private final static int PLUS_EXPIRATION_IN_SECONDS = 60;
    @Override
    public void sendAcceptedVerificationEmail(User userToBeVerified) {
        String message = "Hello " + getFullName(userToBeVerified) + " We are happy to inform you that you're verification process in our application CropTrade are successful you can now list your product and enjoy our services";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("CropTrade user verification")
                .messageBody(message)
                .receiver(userToBeVerified.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending verification email for user " + userToBeVerified.getId() + " success!");
    }

    @Override
    public void sendRejectedVerificationEmail(User rejectedUser, String reason) {
        String message = "Hello " + getFullName(rejectedUser) + " We are sorry to inform you that you're application for verification are rejected by the moderator because " + reason;
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("CropTrade Verification Rejection")
                .messageBody(message)
                .receiver(rejectedUser.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending rejected verification to user with id of " + rejectedUser.getId() + " success!");
    }


    @Override
    public void sendProductListedEmail(User seller, Product product) {
        String message = "Hello " + getFullName(seller) + " We are happy to inform you that you're product listing of " + product.getCrop() + "  are now approved by moderator your product will now be visible in marketplace";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Product listing approve")
                .messageBody(message)
                .receiver(seller.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending product listing for user {} success!", seller.getId());
    }

    @Override
    public void sendRejectedProductEmail(Product product, String reason) {
        User seller = product.getSeller();
        String message = "Hello " + getFullName(seller) + " We are sorry to inform you that you're product listing of " + product.getCrop() + " are rejected by the moderator because " + reason;
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Product listing rejected")
                .messageBody(message)
                .receiver(seller.getUserCredential().getEmail())
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending product rejection email for seller with id {} of in his/her product with id of {} success", seller.getId(), product.getId());
    }

    @Override
    public void sendReleaseWithdrawMail(WithdrawTransaction withdrawTransaction) {
        String receiverEmail = withdrawTransaction.getUser().getUserCredential().getEmail();
        final String message = "Hello " + withdrawTransaction.getUser().getUserDetails().getFirstName() + " we want to inform you that your request for withdrawal transaction amounting " + withdrawTransaction.getAmount() + " has already been release. You can now receive it to successfully complete the transaction. Thank you";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Withdrawal Request Release")
                .messageBody(message)
                .receiver(receiverEmail)
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending release withdrawal request email for {} success", receiverEmail);
    }

    @Override
    public void sendRejectedWithdrawMail(WithdrawTransaction withdrawTransaction) {
        String receiverEmail = withdrawTransaction.getUser().getUserCredential().getEmail();
        final String message = "Hello " + withdrawTransaction.getUser().getUserDetails().getFirstName() + " we want to inform you that you withdrawal request transaction amounting " + withdrawTransaction.getAmount() + " are been rejected.";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Withdrawal Request Rejected")
                .messageBody(message)
                .receiver(receiverEmail)
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending withdrawal rejection mail for {} success", receiverEmail);
    }

    @Override
    public void sendReleaseDepositMail(DepositTransaction depositTransaction) {
        String receiverEmail = depositTransaction.getUser().getUserCredential().getEmail();
        final String message = " Hello " + depositTransaction.getUser().getUserDetails().getFirstName() + " we want to inform you that you're deposit request transaction amounting " + depositTransaction.getAmount() + " has been release. The deposit request will are now reflected in your account";
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Deposit Request Release")
                .messageBody(message)
                .receiver(receiverEmail)
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending release deposit request email for requesting user {} success", receiverEmail);
    }

    @Override
    public void sendRejectDepositMail(DepositTransaction depositTransaction) {
        String receiverEmail = depositTransaction.getUser().getUserCredential().getEmail();
        final String message = "Hello " + depositTransaction.getUser().getUserDetails().getFirstName() + " we want to inform you that you're deposit request transaction has been rejected amounting " + depositTransaction.getAmount();
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Deposit Request Rejected")
                .messageBody(message)
                .receiver(receiverEmail)
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending rejected deposit request email for requesting user {} success!", receiverEmail);
    }

    @Override
    public void sendWelcomeEmail(User registrant) {
        String message = " Hello " + getFullName(registrant) + " Thank you for registering in our application we appreciate your trust. We ensure that you're data is secured and enjoy our services. Thanks...";
        EmailMessage emailMessage = EmailMessage.builder()
                .receiver(registrant.getUserCredential().getEmail())
                .subject("Welcome to CropTrade")
                .messageBody(message)
                .build();
        // emailClient.sendSimpleMail(emailMessage);
        log.debug("Sending registration email for user {} success", registrant.getId());
    }

    @Override
    public OTPMessage sendOTP(User user) {
        OTPMessage otpMessage = OTPMessage.builder()
                .plusExpirationSeconds(PLUS_EXPIRATION_IN_SECONDS)
                .receiver(user.getUserCredential().getEmail())
                .build();
        OTPMessage receiveOTPMessage = emailClient.sendOTPMail(otpMessage);
        log.debug("Sending OTP for user with id of {} success {}", user.getId(), receiveOTPMessage);
        return receiveOTPMessage; // !!! Change this to receiveOTPMessage when you are ready to call this
    }

    private String getFullName(User user) {
        return user.getUserDetails().getFirstName() + " " + user.getUserDetails().getMiddleName() + " " + user.getUserDetails().getLastName();
    }
}
