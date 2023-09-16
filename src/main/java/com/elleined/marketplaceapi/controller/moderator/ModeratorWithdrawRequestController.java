package com.elleined.marketplaceapi.controller.moderator;

import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.mapper.TransactionMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.service.atm.machine.transaction.TransactionService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators/{moderatorId}/withdraw-requests")
public class ModeratorWithdrawRequestController {

    private final ModeratorService moderatorService;

    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;

    @GetMapping
    List<WithdrawTransactionDTO> getAllPendingWithdrawRequest() {
        return moderatorService.getAllPendingWithdrawRequest().stream()
                .map(transactionMapper::toWithdrawTransactionDTO)
                .toList();
    }

    @PatchMapping("/{withdrawTransactionId}/release")
    void releaseWithdrawRequest(@PathVariable("moderatorId") int moderatorId,
                                @PathVariable("withdrawTransactionId") Integer withdrawTransactionId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        WithdrawTransaction withdrawTransaction = transactionService.getWithdrawTransactionById(withdrawTransactionId);
        moderatorService.releaseWithdrawRequest(moderator, withdrawTransaction);
    }

    @PatchMapping("/release")
    void releaseAllWithdrawRequest(@PathVariable("moderatorId") int moderatorId,
                                   @RequestBody Set<Integer> withdrawTransactionIds) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<WithdrawTransaction> withdrawTransactions = new HashSet<>(transactionService.getAllWithdrawTransactions(withdrawTransactionIds));
        moderatorService.releaseAllWithdrawRequest(moderator, withdrawTransactions);
    }

    @PatchMapping("/{withdrawTransactionId}/reject")
    void rejectWithdrawRequest(@PathVariable("moderatorId") int moderatorId,
                               @PathVariable("withdrawTransactionId") Integer withdrawTransactionId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        WithdrawTransaction withdrawTransaction = transactionService.getWithdrawTransactionById(withdrawTransactionId);
        moderatorService.rejectWithdrawRequest(moderator, withdrawTransaction);
    }

    @PatchMapping("/reject")
    void rejectAllWithdrawRequest(@PathVariable("moderatorId") int moderatorId,
                                  @RequestBody Set<Integer> withdrawTransactionIds) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<WithdrawTransaction> withdrawTransactions = new HashSet<>(transactionService.getAllWithdrawTransactions(withdrawTransactionIds));
        moderatorService.rejectAllWithdrawRequest(moderator, withdrawTransactions);
    }
}
