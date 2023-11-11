package com.elleined.marketplaceapi.service.atm.machine.transaction;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositTransactionService implements TransactionService<DepositTransaction> {
    private final DepositTransactionRepository depositTransactionRepository;
    @Override
    public DepositTransaction save(DepositTransaction transaction) {
        return depositTransactionRepository.save(transaction);
    }

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
}
