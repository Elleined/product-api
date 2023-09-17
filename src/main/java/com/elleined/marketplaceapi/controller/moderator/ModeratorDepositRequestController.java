package com.elleined.marketplaceapi.controller.moderator;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.mapper.TransactionMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.service.atm.machine.transaction.TransactionService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators/{moderatorId}/deposit-requests")
public class ModeratorDepositRequestController {
    private final ModeratorService moderatorService;

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    List<DepositTransactionDTO> getAllPendingDepositRequest() {
        return moderatorService.getAllPendingDepositRequest().stream()
                .map(transactionMapper::toDepositTransactionDTO)
                .toList();
    }

    @PatchMapping("/{depositTransactionId}/release")
    void release(@PathVariable("moderatorId") int moderatorId,
                 @PathVariable("depositTransactionId") int depositTransactionId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        DepositTransaction depositTransaction = transactionService.getDepositTransactionById(depositTransactionId);
        moderatorService.release(moderator, depositTransaction);

    }
    @PatchMapping("/release")
    void releaseAllDepositRequest(@PathVariable("moderatorId") int moderatorId,
                                  @RequestBody Set<Integer> depositTransactionsId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<DepositTransaction> depositTransactions = new HashSet<>(transactionService.getAllDepositTransactionById(depositTransactionsId));
        moderatorService.releaseAllDepositRequest(moderator, depositTransactions);
    }
    @PatchMapping("/{depositTransactionId}/reject")
    void reject(@PathVariable("moderatorId") int moderatorId,
                @PathVariable("depositTransactionId") int depositTransactionId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        DepositTransaction depositTransaction = transactionService.getDepositTransactionById(depositTransactionId);
        moderatorService.reject(moderator, depositTransaction);

    }
    @PatchMapping("/reject")
    void rejectAllDepositRequest(@PathVariable("moderatorId") int moderatorId,
                                 @RequestBody Set<Integer> depositTransactionsId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<DepositTransaction> depositTransactions = new HashSet<>(transactionService.getAllDepositTransactionById(depositTransactionsId));
        moderatorService.rejectAllDepositRequest(moderator, depositTransactions);
    }
}
