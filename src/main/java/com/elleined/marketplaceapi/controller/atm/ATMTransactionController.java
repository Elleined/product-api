package com.elleined.marketplaceapi.controller.atm;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.mapper.transaction.DepositTransactionMapper;
import com.elleined.marketplaceapi.mapper.transaction.P2PTransactionMapper;
import com.elleined.marketplaceapi.mapper.transaction.WithdrawTransactionMapper;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.atm.machine.deposit.DepositTransactionService;
import com.elleined.marketplaceapi.service.atm.machine.p2p.P2PTransactionService;
import com.elleined.marketplaceapi.service.atm.machine.withdraw.WithdrawTransactionService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{currentUserId}/atm-transactions")
@RequiredArgsConstructor
public class    ATMTransactionController {
    private final ATMFeeService atmFeeService;
    private final UserService userService;

    private final WithdrawTransactionService withdrawTransactionService;
    private final DepositTransactionService depositTransactionService;
    private final P2PTransactionService p2PTransactionService;

    private final DepositTransactionMapper depositTransactionMapper;
    private final WithdrawTransactionMapper withdrawTransactionMapper;
    private final P2PTransactionMapper p2PTransactionMapper;

    @GetMapping("/withdraw")
    public List<WithdrawTransactionDTO> getAllWithdrawalTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return withdrawTransactionService.getAll(currentUser).stream()
                .map(w -> {
                    float fee = atmFeeService.getWithdrawalFee(w.getAmount());
                    return withdrawTransactionMapper.toDTO(w, fee);
                })
                .toList();
    }

    @GetMapping("/deposit")
    public List<DepositTransactionDTO> getAllDepositTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return depositTransactionService.getAll(currentUser).stream()
                .map(d -> {
                    float fee = atmFeeService.getDepositFee(d.getAmount());
                    return depositTransactionMapper.toDTO(d, fee);
                })
                .toList();
    }

    @GetMapping("/receive-money")
    public List<PeerToPeerTransactionDTO> getAllReceiveMoneyTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return p2PTransactionService.getAllSentMoneyTransactions(currentUser).stream()
                .map(p -> {
                    float fee = atmFeeService.getP2pFee(p.getAmount());
                    return p2PTransactionMapper.toDTO(p, fee);
                })
                .toList();
    }

    @GetMapping("/sent-money")
    public List<PeerToPeerTransactionDTO> getAllSentMoneyTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return p2PTransactionService.getAllSentMoneyTransactions(currentUser).stream()
                .map(p -> {
                    float fee = atmFeeService.getP2pFee(p.getAmount());
                    return p2PTransactionMapper.toDTO(p, fee);
                })
                .toList();
    }
}
