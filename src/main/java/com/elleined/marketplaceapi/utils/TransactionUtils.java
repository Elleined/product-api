package com.elleined.marketplaceapi.utils;

import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.item.OrderItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionUtils {
    static <T extends Transaction> List<T> getTransactionsByDateRange(List<T> transactions, LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
                .filter(transaction -> transaction.getTransactionDate().isEqual(start)
                        || (transaction.getTransactionDate().isAfter(start) && transaction.getTransactionDate().isBefore(end)))
                .toList();
    }
}
