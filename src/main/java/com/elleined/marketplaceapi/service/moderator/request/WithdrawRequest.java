package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WithdrawRequest implements Request<WithdrawTransaction> {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    private final ModeratorRepository moderatorRepository;

    @Override
    public List<WithdrawTransaction> getAllRequest() {
        List<WithdrawTransaction> premiumUsersWithdrawRequest = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(User::hasShopRegistration)
                .filter(User::hasNotBeenRejected) // Checking for rejected user
                .map(User::getWithdrawTransactions)
                .flatMap(Collection::stream)
                .filter(withdrawTransaction -> withdrawTransaction.getStatus() == Transaction.Status.PENDING)
                .toList();

        List<WithdrawTransaction> regularUsersWithdrawRequest = userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(User::hasShopRegistration)
                .filter(User::hasNotBeenRejected) // Checking for rejected user
                .map(User::getWithdrawTransactions)
                .flatMap(Collection::stream)
                .filter(withdrawTransaction -> withdrawTransaction.getStatus() == Transaction.Status.PENDING)
                .toList();

        List<WithdrawTransaction> withdrawTransactions = new ArrayList<>();
        withdrawTransactions.addAll(premiumUsersWithdrawRequest);
        withdrawTransactions.addAll(regularUsersWithdrawRequest);
        return withdrawTransactions;
    }

    @Override
    public void accept(Moderator moderator, WithdrawTransaction withdrawTransaction) {

    }

    @Override
    public void acceptAll(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {

    }

    @Override
    public void reject(Moderator moderator, WithdrawTransaction withdrawTransaction) {

    }

    @Override
    public void rejectAll(Moderator moderator, Set<WithdrawTransaction> withdrawTransactions) {

    }
}
