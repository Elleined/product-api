package com.elleined.marketplaceapi.controller.atm;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.mapper.transaction.DepositTransactionMapper;
import com.elleined.marketplaceapi.mapper.transaction.P2PTransactionMapper;
import com.elleined.marketplaceapi.mapper.transaction.WithdrawTransactionMapper;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.ATMService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/users/{currentUserId}/atm")
@RequiredArgsConstructor
public class ATMController  {
    private final ATMService atmService;
    private final ATMFeeService atmFeeService;
    private final UserService userService;

    private final DepositTransactionMapper depositTransactionMapper;
    private final WithdrawTransactionMapper withdrawTransactionMapper;
    private final P2PTransactionMapper p2PTransactionMapper;

    @PostMapping("/deposit")
    public DepositTransactionDTO requestDeposit(@PathVariable("currentUserId") int currentUserId,
                                                @RequestParam("amount") BigDecimal amount,
                                                @RequestPart("proofOfTransaction") MultipartFile proofOfTransaction) throws IOException {

        User currentUser = userService.getById(currentUserId);
        DepositTransaction depositTransaction = atmService.requestDeposit(currentUser, amount, proofOfTransaction);
        float fee = atmFeeService.getDepositFee(depositTransaction.getAmount());
        return depositTransactionMapper.toDTO(depositTransaction, fee);
    }


    @PostMapping("/withdraw")
    public WithdrawTransactionDTO requestWithdraw(@PathVariable("currentUserId") int currentUserId,
                                                  @RequestParam("amount") BigDecimal amount,
                                                  @RequestParam("gcashNumber") String gcashNumber) {

        User currentUser = userService.getById(currentUserId);
        WithdrawTransaction withdrawTransaction = atmService.requestWithdraw(currentUser, amount, gcashNumber);
        float fee = atmFeeService.getWithdrawalFee(withdrawTransaction.getAmount());
        return withdrawTransactionMapper.toDTO(withdrawTransaction, fee);
    }

    @PostMapping("/send-money/{receiverId}")
    public PeerToPeerTransactionDTO peerToPeer(@PathVariable("currentUserId") int senderId,
                                               @RequestParam("amount") BigDecimal sentAmount,
                                               @PathVariable("receiverId") int receiverId) {

        User sender = userService.getById(senderId);
        User receiver = userService.getById(receiverId);
        PeerToPeerTransaction peerToPeerTransaction = atmService.peerToPeer(sender, receiver, sentAmount);
        float fee = atmFeeService.getP2pFee(peerToPeerTransaction.getAmount());
        return p2PTransactionMapper.toDTO(peerToPeerTransaction, fee);
    }
}
