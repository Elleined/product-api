//package com.elleined.marketplaceapi.controller.atm;
//
//import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
//import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
//import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
//import com.elleined.marketplaceapi.mapper.transaction.TransactionMapper;
//import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
//import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
//import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
//import com.elleined.marketplaceapi.model.user.User;
//import com.elleined.marketplaceapi.service.atm.ATMService;
//import com.elleined.marketplaceapi.service.user.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//
//@RestController
//@RequestMapping("/users/{currentUserId}/atm")
//@RequiredArgsConstructor
//public class ATMController  {
//    private final ATMService atmService;
//    private final UserService userService;
//    private final TransactionMapper transactionMapper;
//
//    @PostMapping("/deposit")
//    public DepositTransactionDTO requestDeposit(@PathVariable("currentUserId") int currentUserId,
//                                                @RequestParam("amount") BigDecimal amount,
//                                                @RequestPart("proofOfTransaction") MultipartFile proofOfTransaction) throws IOException {
//
//        User currentUser = userService.getById(currentUserId);
//        DepositTransaction depositTransaction = atmService.requestDeposit(currentUser, amount, proofOfTransaction);
//        return transactionMapper.toDepositTransactionDTO(depositTransaction);
//    }
//
//
//    @PostMapping("/withdraw")
//    public WithdrawTransactionDTO requestWithdraw(@PathVariable("currentUserId") int currentUserId,
//                                                  @RequestParam("amount") BigDecimal amount,
//                                                  @RequestParam("gcashNumber") String gcashNumber) {
//
//        User currentUser = userService.getById(currentUserId);
//        WithdrawTransaction withdrawTransaction = atmService.requestWithdraw(currentUser, amount, gcashNumber);
//        return transactionMapper.toWithdrawTransactionDTO(withdrawTransaction);
//    }
//
//    @PostMapping("/send-money/{receiverId}")
//    public PeerToPeerTransactionDTO peerToPeer(@PathVariable("currentUserId") int senderId,
//                                               @RequestParam("amount") BigDecimal sentAmount,
//                                               @PathVariable("receiverId") int receiverId) {
//
//        User sender = userService.getById(senderId);
//        User receiver = userService.getById(receiverId);
//        PeerToPeerTransaction peerToPeerTransaction = atmService.peerToPeer(sender, receiver, sentAmount);
//        return transactionMapper.toPeer2PeerTransactionDTO(peerToPeerTransaction);
//    }
//}
