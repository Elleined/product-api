package com.elleined.marketplaceapi.service.atm.machine.withdraw;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WithdrawTransactionServiceImpl implements WithdrawTransactionService {
    private final WithdrawTransactionRepository withdrawTransactionRepository;

    @Override
    public WithdrawTransaction getById(int id) throws ResourceNotFoundException {
        return withdrawTransactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction with id of " + id + " does't exists!"));
    }

    @Override
    public List<WithdrawTransaction> getAllById(Set<Integer> ids) {
        return withdrawTransactionRepository.findAllById(ids);
    }

    @Override
    public List<WithdrawTransaction> getAll(User currentUser) {
        return currentUser.getWithdrawTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public WithdrawTransaction save(User user, BigDecimal withdrawalAmount, String gcashNumber) {
        String trn = UUID.randomUUID().toString();

        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .trn(trn)
                .amount(withdrawalAmount)
                .transactionDate(LocalDateTime.now())
                .status(Transaction.Status.PENDING)
                .gcashNumber(gcashNumber)
                .user(user)
                .build();
        withdrawTransactionRepository.save(withdrawTransaction);
        return withdrawTransaction;
    }
}
