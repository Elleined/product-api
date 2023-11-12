package com.elleined.marketplaceapi.service.atm.machine.deposit;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
public class DepositTransactionServiceImpl implements DepositTransactionService {
    private final DepositTransactionRepository depositTransactionRepository;

    @Override
    public DepositTransaction getById(int id) throws ResourceNotFoundException {
        return depositTransactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction with id of " + id + " does't exists!"));
    }

    @Override
    public List<DepositTransaction> getAllById(Set<Integer> ids) {
        return depositTransactionRepository.findAllById(ids);
    }

    @Override
    public List<DepositTransaction> getAll(User currentUser) {
        return currentUser.getDepositTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .toList();
    }

    @Override
    public DepositTransaction save(User user, BigDecimal depositedAmount, MultipartFile proofOfTransaction) {
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .trn(UUID.randomUUID().toString())
                .amount(depositedAmount)
                .transactionDate(LocalDateTime.now())
                .status(Transaction.Status.PENDING)
                .proofOfTransaction(proofOfTransaction.getOriginalFilename())
                .user(user)
                .build();
        depositTransactionRepository.save(depositTransaction);

        return depositTransaction;
    }
}
