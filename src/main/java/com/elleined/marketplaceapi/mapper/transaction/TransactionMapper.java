package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.TransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;

public interface TransactionMapper<T extends Transaction> {
    TransactionDTO toDTO(T t, float fee);
}
