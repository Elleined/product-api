package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;
import java.util.Set;

public interface TransactionService<T extends Transaction> {
    T getById(int id) throws ResourceNotFoundException;
    List<T> getAllById(Set<Integer> ids);
    List<T> getAll(User currentUser);

}
