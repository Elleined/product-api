package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.model.user.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ATMValidator {

    public boolean isBalanceEnough(User sender, BigDecimal amountToBeSent) {
        return sender.getBalance().compareTo(amountToBeSent) < 0;
    }

    public boolean isValidAmount(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isSenderSendingToHimself(User sender, User receiver) {
        return sender.getId() == receiver.getId();
    }
}
